package pl.starchasers.mdpages.authentication.dto

import javax.validation.constraints.NotBlank

class TokenDTO(
    /**
     * JWT token
     */
    @field:NotBlank
    val token: String
)