package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.content.MdObjectType
import pl.starchasers.mdpages.util.BasicResponseDTO
import java.time.LocalDateTime

class PageDetailsResponseDTO(
    val id: Long,
    val name: String,
    val content: String,
    val dateCreated: LocalDateTime,
    val dateModified: LocalDateTime,
    val type: MdObjectType = MdObjectType.PAGE,
    val parentFolderId: Long,
    val scopeFolderId: Long
) : BasicResponseDTO()