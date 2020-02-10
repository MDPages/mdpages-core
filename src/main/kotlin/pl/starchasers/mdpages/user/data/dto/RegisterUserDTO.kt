package pl.starchasers.mdpages.user.data.dto

import com.sun.istack.NotNull
import javax.validation.constraints.Email

class RegisterUserDTO(
    @field:NotNull
    val username: String,

    @field:NotNull
    val password: String,

    @field:NotNull
    @field:Email
    val email: String
)