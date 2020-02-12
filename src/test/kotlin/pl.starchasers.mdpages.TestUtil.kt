import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.springframework.test.web.servlet.ResultActionsDsl

fun errorThrown(result: ResultActionsDsl) = result.andExpect {
    status { is4xxClientError }
    jsonPath("$.errorMessage", notNullValue())
    jsonPath("$.success", equalTo(false))
}

fun success(result: ResultActionsDsl) = result.andExpect {
    status { isOk }
    jsonPath("$.success", equalTo(true))
}