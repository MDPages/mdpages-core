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
import pl.starchasers.mdpages.MockMvcTestBase
import pl.starchasers.mdpages.authentication.dto.LoginDTO
import pl.starchasers.mdpages.authentication.dto.TokenDTO
import pl.starchasers.mdpages.isError
import pl.starchasers.mdpages.isSuccess
import pl.starchasers.mdpages.user.UserService


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
internal class AuthenticationControllerTest(
    @Autowired private val userService: UserService,
    @Autowired private val tokenService: TokenService
) : MockMvcTestBase() {

    @BeforeAll
    fun createTestUser() {
        userService.createUser("testUser", "passw0rd")
    }

    @Nested
    @DisplayName("/api/auth/login endpoint")
    inner class Login() {
        private val loginRequestPath = Path("/api/auth/login")

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

    @Nested
    inner class GetAccessToken {

        private var refreshToken = ""
        private val getAccessTokenRequestPath = Path("/api/auth/getAccessToken")

        @BeforeEach
        fun issueTestRefreshToken() {
            refreshToken = tokenService.issueRefreshToken(userService.getUserByUsername("testUser"))
        }

        @AfterEach
        fun removeTestRefreshToken() {
            tokenService.invalidateRefreshToken(refreshToken)
        }

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

    @Nested
    inner class RefreshToken {
        private var refreshToken = ""
        private val refreshTokenRequestPath = Path("/api/auth/refreshToken")

        @BeforeEach
        fun issueTestRefreshToken() {
            refreshToken = tokenService.issueRefreshToken(userService.getUserByUsername("testUser"))
        }

        @AfterEach
        fun removeTestRefreshToken() {
            tokenService.invalidateRefreshToken(refreshToken)
        }


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