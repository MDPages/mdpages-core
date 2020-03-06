package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.util.BasicResponseDTO

class FolderResponseWrapperDTO(
    /**
     * Requested folder details
     */
    val folder: FolderResponseDTO
) : BasicResponseDTO()