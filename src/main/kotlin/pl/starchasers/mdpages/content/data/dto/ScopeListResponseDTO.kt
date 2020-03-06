package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.util.BasicResponseDTO

class ScopeListResponseDTO(
    /**
     * List of scopes readable by current user.
     */
    val scopes: List<ScopeResponseDTO>
) : BasicResponseDTO()