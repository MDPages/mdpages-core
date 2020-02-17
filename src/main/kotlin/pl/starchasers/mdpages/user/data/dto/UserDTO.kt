package pl.starchasers.mdpages.user.data.dto

import javax.validation.constraints.NotBlank

class UserDTO(
    /**
     * Valid user's username
     */
    @field:NotBlank val username: String
)