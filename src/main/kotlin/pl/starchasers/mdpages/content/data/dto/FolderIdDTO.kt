package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.util.BasicResponseDTO

data class FolderIdResponseDTO(
    /**
     * Valid folder id
     */
    val folderId: Long
) : BasicResponseDTO(){

}