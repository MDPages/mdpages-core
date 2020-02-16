package pl.starchasers.mdpages.util

open class BasicErrorResponseDTO(
    /**
     * Present only when error occured
     */
    val errorMessage: String = "Error occurred!"
) : BasicResponseDTO(false)