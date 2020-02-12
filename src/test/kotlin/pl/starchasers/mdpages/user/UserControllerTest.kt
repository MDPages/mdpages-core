package pl.starchasers.mdpages.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import pl.starchasers.mdpages.errorThrown
import pl.starchasers.mdpages.success
import pl.starchasers.mdpages.user.data.dto.RegisterUserDTO

@SpringBootTest
@AutoConfigureMockMvc
internal class UserControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val mapper: ObjectMapper,
    @Autowired private val userService: UserService
) {
    @Nested
    inner class RegisterUser() {

        @AfterEach
        fun removeTestUser(){
            userService.deleteUser("testUser")
        }

        @Test
        fun `Given valid data, should return success and register user`() {
            mockMvc.post("/api/user/register") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(RegisterUserDTO("testUser", "passw0rd", "asd@asd.pl"))
            }.andDo { print() }.let { success(it) }

            userService.getUserByUsername("testUser").run {
                assertEquals(username, "testUser")
                assertEquals(email, "asd@asd.pl")
            }
        }

        @Test
        fun `Given duplicate username, should return 400`() {
            userService.createUser("testUser", "password")

            mockMvc.post("/api/user/register") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(RegisterUserDTO("testUser", "passw0rd", "asd@asd.pl"))
            }.andDo { print() }.let { errorThrown(it) }
        }

        @Test
        fun `Given too short password, should return 400`() {
            mockMvc.post("/api/user/register") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(RegisterUserDTO("testUser", "passw0r", "asd@asd.pl"))
            }.andDo { print() }.let { errorThrown(it) }
        }

        @Test
        fun `Given too long username, should return 400`() {
            mockMvc.post("/api/user/register") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(RegisterUserDTO("a".repeat(64), "passw0rd", "asd@asd.pl"))
            }.andDo { print() }.let { errorThrown(it) }
        }
    }
}