package pl.starchasers.mdpages.admin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@WebAppConfiguration
internal class AdminControllerTest(
    @Autowired private val webApplicationContext: WebApplicationContext
) {
    private lateinit var mockMvc: MockMvc


    @BeforeEach
    fun createMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Nested
    inner class CreateUser {

        fun `Given valid data, should return success and create user`() {

        }

        fun `Given unauthorized user, should return 401`() {

        }

        fun `Given unauthenticated user, should return 400`() {

        }

        fun `Given duplicate username, should return 400`() {

        }


    }
}