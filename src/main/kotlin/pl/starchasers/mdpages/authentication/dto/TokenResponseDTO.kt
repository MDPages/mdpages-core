package pl.starchasers.mdpages.authentication.dto

import pl.starchasers.mdpages.util.BasicResponseDTO

class TokenResponseDTO(
    /**
     * JWT token
     */
    val token: String
) : BasicResponseDTO()