package pl.starchasers.mdpages.authentication

import no.skatteetaten.aurora.mockmvc.extensions.Path
import no.skatteetaten.aurora.mockmvc.extensions.contentTypeJson
import no.skatteetaten.aurora.mockmvc.extensions.post
import no.skatteetaten.aurora.mockmvc.extensions.responseJsonPath
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.*
import pl.starchasers.mdpages.authentication.dto.LoginDTO
import pl.starchasers.mdpages.authentication.dto.TokenDTO
import pl.starchasers.mdpages.user.UserService


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
internal class AuthenticationControllerTest(
    @Autowired private val userService: UserService,
    @Autowired private val tokenService: TokenService
) : MockMvcTestBase() {

    @BeforeEach
    fun createTestUser() {
        userService.createUser("testUser", "passw0rd")
    }

    @Transactional
    @OrderTests
    @Nested
    inner class Login() :MockMvcTestBase(){
        private val loginRequestPath = Path("/api/auth/login")

        @DocumentResponse
        @Test
        fun `Given valid data, should return refresh token`() {
            mockMvc.post(
                path = loginRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(LoginDTO("testUser", "passw0rd"))
            ) {
                isSuccess()
                responseJsonPath("$.token").isNotEmpty()
            }
            //TODO verify token in db
        }

        @Test
        fun `Given incorrect password, should return 401`() {
            mockMvc.post(
                path = loginRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(LoginDTO("testUser", "pasword"))
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }

        }

        @Test
        fun `Given missing fields, should return 400`() {
            mockMvc.post(
                path = loginRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = "{}"
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }

        }
    }

    @Transactional
    @OrderTests
    @Nested
    inner class GetAccessToken :MockMvcTestBase(){

        private var refreshToken = ""
        private val getAccessTokenRequestPath = Path("/api/auth/getAccessToken")

        @BeforeEach
        fun issueTestRefreshToken() {
            refreshToken = tokenService.issueRefreshToken(userService.getUserByUsername("testUser"))
        }

        fun removeTestRefreshToken() {
            tokenService.invalidateRefreshToken(refreshToken)
        }

        @DocumentResponse
        @Test
        fun `Given valid refresh token, should return access token`() {
            mockMvc.post(
                path = getAccessTokenRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(TokenDTO(refreshToken))
            ) {
                isSuccess()
                responseJsonPath("$.token").isNotEmpty()
            }

        }

        @Test
        fun `Given invalid token, should return 401`() {
            removeTestRefreshToken()

            mockMvc.post(
                path = getAccessTokenRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(TokenDTO(refreshToken))
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }

    }

    @Transactional
    @OrderTests
    @Nested
    inner class RefreshToken ():MockMvcTestBase(){
        private var refreshToken = ""
        private val refreshTokenRequestPath = Path("/api/auth/refreshToken")

        @BeforeEach
        fun issueTestRefreshToken() {
            refreshToken = tokenService.issueRefreshToken(userService.getUserByUsername("testUser"))
        }

        fun removeTestRefreshToken() {
            tokenService.invalidateRefreshToken(refreshToken)
        }


        @DocumentResponse
        @Test
        fun `Given valid refresh token, should return new refresh token`() {
            mockMvc.post(
                path = refreshTokenRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(TokenDTO(refreshToken))
            ) {
                isSuccess()
                responseJsonPath("$.token").isNotEmpty()
            }
            //TODO verify token in db
        }

        @Test
        fun `Given invalid refresh token, should return 401`() {
            removeTestRefreshToken()

            mockMvc.post(
                path = refreshTokenRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(TokenDTO(refreshToken))
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }
    }

    @Nested
    inner class Logout() {
        //TODO implement
    }

}