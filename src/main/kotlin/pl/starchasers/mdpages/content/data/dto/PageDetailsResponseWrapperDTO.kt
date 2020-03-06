package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.util.BasicResponseDTO

class PageDetailsResponseWrapperDTO(
    /**
     * Requested page details
     */
    val page: PageDetailsResponseDTO
):BasicResponseDTO()