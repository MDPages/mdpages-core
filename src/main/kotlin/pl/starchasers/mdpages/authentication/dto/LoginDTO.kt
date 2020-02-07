package pl.starchasers.mdpages.authentication.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class LoginDTO(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)