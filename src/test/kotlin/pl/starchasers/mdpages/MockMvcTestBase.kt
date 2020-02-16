package pl.starchasers.mdpages

import capital.scalable.restdocs.AutoDocumentation
import capital.scalable.restdocs.jackson.JacksonResultHandlers
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.cli.CliDocumentation
import org.springframework.restdocs.http.HttpDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@ExtendWith(RestDocumentationExtension::class)
@SpringBootTest
@WebAppConfiguration
abstract class MockMvcTestBase {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext
    @Autowired
    protected lateinit var mapper: ObjectMapper

    protected lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//            .addFilters<DefaultMockMvcBuilder>(springSecurityFilterChain)
            .alwaysDo<DefaultMockMvcBuilder>(JacksonResultHandlers.prepareJackson(mapper))
//            .alwaysDo<DefaultMockMvcBuilder>(commonDocumentation())
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                    .uris()
                    .withScheme("http")
                    .withHost("localhost")
                    .withPort(8080)
                    .and().snippets()
                    .withDefaults(
                        CliDocumentation.curlRequest(),
                        HttpDocumentation.httpRequest(),
                        HttpDocumentation.httpResponse(),
                        AutoDocumentation.requestFields(),
                        AutoDocumentation.responseFields(),
                        AutoDocumentation.pathParameters(),
                        AutoDocumentation.requestParameters(),
                        AutoDocumentation.description(),
                        AutoDocumentation.methodAndPath(),
                        AutoDocumentation.section()
                    )
            )
            .build()
    }
}