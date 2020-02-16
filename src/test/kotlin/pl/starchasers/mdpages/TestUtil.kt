package pl.starchasers.mdpages

import no.skatteetaten.aurora.mockmvc.extensions.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.ResultActionsDsl

fun MockMvcData.isSuccess() {
    printResponseBody()
    statusIsOk()
    responseJsonPath("$.success").isTrue()
}

fun MockMvcData.isError(expectedStatus: HttpStatus) {
    printResponseBody()
    status(expectedStatus)
    responseJsonPath("$.success").isFalse()
    responseJsonPath("$.errorMessage").isNotEmpty()
}


fun errorThrown(result: ResultActionsDsl) = result.andExpect {
    status { is4xxClientError }
    jsonPath("$.errorMessage", notNullValue())
    jsonPath("$.success", equalTo(false))
}

fun success(result: ResultActionsDsl) = result.andExpect {
    status { isOk }
    jsonPath("$.success", equalTo(true))
}