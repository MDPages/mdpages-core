package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.content.MdObjectType

class PageBriefResponseDTO(
    name: String,
    id: Long
) : MdObjectResponseDTO(name, id, MdObjectType.PAGE)