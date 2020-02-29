package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.content.MdObjectType

class FolderResponseDTO(
    name: String,
    id: Long,
    val children: Set<MdObjectResponseDTO>
) : MdObjectResponseDTO(name, id, MdObjectType.FOLDER)