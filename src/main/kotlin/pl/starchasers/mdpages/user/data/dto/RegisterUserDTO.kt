package pl.starchasers.mdpages.user.data.dto

import com.sun.istack.NotNull
import javax.validation.constraints.Email

class RegisterUserDTO(
    /**
     * Must be between 3 and 32 alphanumeric characters
     */
    @field:NotNull
    val username: String,

    /**
     * Must be between 8 and 64 characters
     */
    @field:NotNull
    val password: String,

    @field:NotNull
    @field:Email
    val email: String
)