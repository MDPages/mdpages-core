package pl.starchasers.mdpages.user

import no.skatteetaten.aurora.mockmvc.extensions.Path
import no.skatteetaten.aurora.mockmvc.extensions.authorization
import no.skatteetaten.aurora.mockmvc.extensions.contentTypeJson
import no.skatteetaten.aurora.mockmvc.extensions.post
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.*
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.user.data.dto.ChangePasswordDTO
import pl.starchasers.mdpages.user.data.dto.RegisterUserDTO

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
internal class UserControllerTest(
    @Autowired private val userService: UserService,
    @Autowired private val tokenService: TokenService
) : MockMvcTestBase() {

    private var exampleUserId: Long = -1

    @BeforeEach
    fun createExampleUser() {
        exampleUserId = userService.createUser("exampleUser", "passw0rd").id
    }

    fun getAccessToken(): String =
        tokenService.issueAccessToken(tokenService.issueRefreshToken(userService.getUser(exampleUserId)))

    @Transactional
    @OrderTests
    @Nested
    inner class RegisterUser() : MockMvcTestBase() {

        private val registerRequestPath = Path("/api/user/register")

        @DocumentResponse
        @Test
        fun `Given valid data, should return success and register user`() {
            mockMvc.post(
                path = registerRequestPath,
                body = mapper.writeValueAsString(RegisterUserDTO("testUser", "passw0rd", "asd@asd.pl")),
                headers = HttpHeaders().contentTypeJson()
            ) {
                isSuccess()
            }

            userService.getUserByUsername("testUser").run {
                assertEquals(username, "testUser")
                assertEquals(email, "asd@asd.pl")
            }
        }

        @Test
        fun `Given duplicate username, should return 400`() {
            userService.createUser("testUser", "password")

            mockMvc.post(
                path = registerRequestPath,
                body = mapper.writeValueAsString(RegisterUserDTO("testUser", "passw0rd", "asd@asd.pl")),
                headers = HttpHeaders().contentTypeJson()
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
        }

        @Test
        fun `Given too short password, should return 400`() {
            mockMvc.post(
                path = registerRequestPath,
                body = mapper.writeValueAsString(RegisterUserDTO("testUser", "passw0r", "asd@asd.pl")),
                headers = HttpHeaders().contentTypeJson()
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
        }

        @Test
        fun `Given too long username, should return 400`() {
            mockMvc.post(
                path = registerRequestPath,
                body = mapper.writeValueAsString(RegisterUserDTO("a".repeat(64), "passw0rd", "asd@asd.pl")),
                headers = HttpHeaders().contentTypeJson()
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
        }
    }

    @Transactional
    @OrderTests
    @Nested
    inner class ChangePassword(@Autowired private val passwordEncoder: Pbkdf2PasswordEncoder) : MockMvcTestBase() {
        val changePasswordRequest = Path("/api/user/changePassword")

        @DocumentResponse
        @Test
        fun `Given valid data, should change password and return 200`() {
            mockMvc.post(
                path = changePasswordRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(ChangePasswordDTO("passw0rd", "newPassword"))
            ) {
                isSuccess()
            }
            flush()
            userService.getUser(exampleUserId).apply {
                assertTrue(passwordEncoder.matches("newPassword", password))
            }
        }

        @Test
        fun `Given invalid current password, should do nothing and return 400`() {
            mockMvc.post(
                path = changePasswordRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(ChangePasswordDTO("passw0rdd", "newPassword"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
            userService.getUser(exampleUserId).apply {
                assertTrue(passwordEncoder.matches("passw0rd", password))
            }
        }

        @Test
        fun `Given unchanged password, should do nothing and return 400`() {
            mockMvc.post(
                path = changePasswordRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(ChangePasswordDTO("passw0rd", "passw0rd"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
        }

        @Test
        fun `Given too short password, should do nothing and return 400`() {
            mockMvc.post(
                path = changePasswordRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(ChangePasswordDTO("passw0rd", "new"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
            userService.getUser(exampleUserId).apply {
                assertTrue(passwordEncoder.matches("passw0rd", password))
            }
        }

        @Test
        fun `Given unauthenticated user, should do nothing and return 400`() {
            mockMvc.post(
                path = changePasswordRequest,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(ChangePasswordDTO("passw0rd", "newPassword"))
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            userService.getUser(exampleUserId).apply {
                assertTrue(passwordEncoder.matches("newPassword", password))
            }
        }
    }
}