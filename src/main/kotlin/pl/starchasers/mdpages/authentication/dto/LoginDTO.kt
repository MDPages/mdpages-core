package pl.starchasers.mdpages.authentication.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class LoginDTO(
    /**
     * User's username
     */
    @field:NotBlank
    val username: String,
    /**
     * User's password, in plaintext
     */
    @field:NotBlank
    val password: String
)