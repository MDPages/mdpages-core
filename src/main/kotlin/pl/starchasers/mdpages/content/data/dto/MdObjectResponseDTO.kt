package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.content.MdObjectType
import pl.starchasers.mdpages.util.BasicResponseDTO

abstract class MdObjectResponseDTO(
    val name: String,
    val id: Long,
    val type: MdObjectType
) : BasicResponseDTO()