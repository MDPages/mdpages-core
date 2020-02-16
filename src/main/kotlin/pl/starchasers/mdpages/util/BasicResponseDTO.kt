package pl.starchasers.mdpages.util

import com.fasterxml.jackson.annotation.JsonSubTypes

@JsonSubTypes(JsonSubTypes.Type(value = BasicErrorResponseDTO::class, name = "BasicErrorResponse"))
open class BasicResponseDTO(
    val success: Boolean = true
)