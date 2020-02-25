package pl.starchasers.mdpages

import no.skatteetaten.aurora.mockmvc.extensions.*
import org.hamcrest.Matchers
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

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

//TODO make library pull request from this
//https://github.com/spring-projects/spring-framework/issues/21129
//https://github.com/Skatteetaten/mockmvc-extensions-kotlin/blob/master/src/main/kotlin/no/skatteetaten/aurora/mockmvc/extensions/mockMvcAssertions.kt
fun JsonPathEquals.equalsLong(value: Long): ResultActions =
    resultActions.andExpect(jsonPath<Long>(expression, Matchers.equalTo<Long>(value), Long::class.java))

/**
 * Response from this test will be included as "Example Response" when generating REST documentation.
 * If none method is annotated, one is chosen at random.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DocumentResponse

/**
 * Sorts test execution, so those annotated with DocumentResponse will be executed last
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestMethodOrder(AnnotationMethodOrderer::class)
annotation class OrderTests