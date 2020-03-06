package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.content.MdObjectType

class FolderResponseDTO(
    name: String,
    id: Long,
    /**
     * Objects in this folder. There might be folders, pages and files. Exact object type is determined by the "type" field
     */
    val children: Set<MdObjectResponseDTO>
) : MdObjectResponseDTO(name, id, MdObjectType.FOLDER)