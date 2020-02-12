package pl.starchasers.mdpages.admin

import com.fasterxml.jackson.databind.ObjectMapper
import errorThrown
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.starchasers.mdpages.admin.data.CreateUserDTO
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.security.permission.PermissionService
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.user.data.User
import success

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
internal class AdminControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val mapper: ObjectMapper,
    @Autowired private val userService: UserService,
    @Autowired private val tokenService: TokenService,
    @Autowired private val permissionService: PermissionService
) {
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


    @Nested
    inner class CreateUser {

        @AfterEach
        fun deleteTestUsers() {
            userService.deleteUser("createdUser")
        }

        @Test
        fun `Given valid data, should return success and create user`() {
            mockMvc.post("/api/admin/user") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CreateUserDTO("createdUser", "password"))
                headers { setBearerAuth(getAccessToken(testAdmin)) }
            }.andDo { print() }.let { success(it) }
        }

        @Test
        fun `Given duplicate username, should return 400`() {
            mockMvc.post("/api/admin/user") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CreateUserDTO("testUser", "password"))
                headers { setBearerAuth(getAccessToken(testAdmin)) }
            }.andDo { print() }.let { errorThrown(it) }
        }

        @Test
        fun `Given unauthorized user, should return 401`() {
            mockMvc.post("/api/admin/user") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CreateUserDTO("createdUser", "password"))
                headers { setBearerAuth(getAccessToken(testUser)) }
            }.andDo { print() }.let { errorThrown(it) }
        }

        @Test
        fun `Given unauthenticated user, should return 400`() {
            mockMvc.post("/api/admin/user") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CreateUserDTO("createdUser", "password"))
            }.andDo { print() }.let { errorThrown(it) }
        }


    }
}