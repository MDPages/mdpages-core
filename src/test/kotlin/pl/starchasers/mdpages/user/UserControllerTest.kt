package pl.starchasers.mdpages.user

import no.skatteetaten.aurora.mockmvc.extensions.Path
import no.skatteetaten.aurora.mockmvc.extensions.contentTypeJson
import no.skatteetaten.aurora.mockmvc.extensions.post
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.*
import pl.starchasers.mdpages.user.data.dto.RegisterUserDTO

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
internal class UserControllerTest(
    @Autowired private val userService: UserService
) : MockMvcTestBase() {

    @Transactional
    @OrderTests
    @Nested
    inner class RegisterUser():MockMvcTestBase() {

        private val registerRequestPath = Path("/api/user/register")


        @AfterEach
        fun removeTestUser() {
            userService.deleteUser("testUser")
        }

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
}