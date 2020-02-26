package pl.starchasers.mdpages.user.data.dto

import javax.validation.constraints.NotEmpty

data class ChangePasswordDTO(
    /**
     * Current password, in plaintext
     */
    @field:NotEmpty
    val oldPassword: String,

    /**
     * New password, in plaintext. Must be between 8 and 64 characters
     */
    @field:NotEmpty
    val newPassword: String
)