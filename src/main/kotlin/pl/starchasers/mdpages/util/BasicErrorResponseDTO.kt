package pl.starchasers.mdpages.util

open class BasicErrorResponseDTO(
    /**
     * Present only when error occured and success=false
     */
    val errorMessage: String? = "Error occurred!",

    /**
     * True only when request was completed successfully
     */
    val success: Boolean = false
)