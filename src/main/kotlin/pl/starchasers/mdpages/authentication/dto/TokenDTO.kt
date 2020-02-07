package pl.starchasers.mdpages.authentication.dto

import javax.validation.constraints.NotBlank

class TokenDTO(
    @field:NotBlank
    val token: String
)