package pl.starchasers.mdpages.authentication.dto

import pl.starchasers.mdpages.util.BasicErrorResponseDTO
import pl.starchasers.mdpages.util.BasicResponseDTO

class TokenResponseDTO(
    val token: String
) : BasicResponseDTO()