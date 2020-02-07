package pl.starchasers.mdpages.user.data.dto

import javax.validation.constraints.NotBlank

class UserDTO(
    @field:NotBlank val username: String
)