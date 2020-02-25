package pl.starchasers.mdpages.admin.data

import com.sun.istack.NotNull

class CreateUserDTO(
    /**
     * Must be between 3 and 32 alphanumeric characters
     */
    @field:NotNull
    val username: String,

    /**
     * Must be between 8 and 64 characters
     */
    @field:NotNull
    val password: String
)