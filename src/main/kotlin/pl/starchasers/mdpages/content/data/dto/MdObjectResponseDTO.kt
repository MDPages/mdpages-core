package pl.starchasers.mdpages.content.data.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import pl.starchasers.mdpages.content.MdObjectType

@JsonSubTypes(
    JsonSubTypes.Type(FolderResponseDTO::class),
    JsonSubTypes.Type(PageBriefResponseDTO::class)
)
abstract class MdObjectResponseDTO(
    /**
     * Object name or page title
     */
    val name: String,
    /**
     * Object id
     */
    val id: Long,
    val type: MdObjectType
)