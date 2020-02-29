package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.content.MdObjectType
import java.time.LocalDateTime

class PageDetailsResponseDTO(
    val id: Long,
    val name: String,
    val content: String,
    val dateCreated: LocalDateTime,
    val modified: LocalDateTime,
    val type: MdObjectType = MdObjectType.PAGE
)