package pl.starchasers.mdpages.admin

import no.skatteetaten.aurora.mockmvc.extensions.Path
import no.skatteetaten.aurora.mockmvc.extensions.authorization
import no.skatteetaten.aurora.mockmvc.extensions.contentTypeJson
import no.skatteetaten.aurora.mockmvc.extensions.post
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import pl.starchasers.mdpages.*
import pl.starchasers.mdpages.admin.data.CreateUserDTO
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.security.permission.PermissionService
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.user.data.User

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AdminControllerTest(
    @Autowired private val userService: UserService,
    @Autowired private val tokenService: TokenService,
    @Autowired private val permissionService: PermissionService
) : MockMvcTestBase() {
    private lateinit var testAdmin: User
    private lateinit var testUser: User

    @BeforeAll
    fun createTestAdmin() {
        userService.createUser("testAdmin", "password")
        testAdmin = userService.getUserByUsername("testAdmin")
        permissionService.grantGlobalPermission(PermissionType.ADMIN, testAdmin)
    }

    @BeforeAll
    fun createTestUser() {
        userService.createUser("testUser", "password")
        testUser = userService.getUserByUsername("testUser")
    }

    fun getAccessToken(user: User) =
        tokenService.issueRefreshToken(user).let { refreshToken -> tokenService.issueAccessToken(refreshToken) }


    @OrderTests
    @Nested
    inner class CreateUser {

        private val createUserRequestPath = Path("/api/admin/user")

        @AfterEach
        fun deleteTestUsers() {
            userService.deleteUser("createdUser")
        }

        @DocumentResponse
        @Test
        fun `Given valid data, should return success and create user`() {
            mockMvc.post(
                path = createUserRequestPath,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken(testAdmin)),
                body = mapper.writeValueAsString(CreateUserDTO("createdUser", "password"))
            ) {
                isSuccess()
            }
        }

        @Test
        fun `Given duplicate username, should return 400`() {
            mockMvc.post(
                path = createUserRequestPath,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken(testAdmin)),
                body = mapper.writeValueAsString(CreateUserDTO("testUser", "password"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
        }

        @Test
        fun `Given unauthorized user, should return 401`() {
            mockMvc.post(
                path = createUserRequestPath,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken(testUser)),
                body = mapper.writeValueAsString(CreateUserDTO("createdUser", "password"))
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }

        @Test
        fun `Given unauthenticated user, should return 401`() {
            mockMvc.post(
                path = createUserRequestPath,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(CreateUserDTO("createdUser", "password"))
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }


    }
}