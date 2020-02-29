package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.content.MdObjectType

abstract class MdObjectResponseDTO(
    val name: String,
    val id: Long,
    val type: MdObjectType
)