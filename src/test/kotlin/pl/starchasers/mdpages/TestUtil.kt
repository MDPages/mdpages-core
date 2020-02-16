package pl.starchasers.mdpages

import no.skatteetaten.aurora.mockmvc.extensions.*
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.HttpStatus

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