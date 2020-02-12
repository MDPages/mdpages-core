package pl.starchasers.mdpages.admin.data

import com.sun.istack.NotNull

class CreateUserDTO(
    @field:NotNull
    val username: String,

    @field:NotNull
    val password: String
)