package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.util.BasicResponseDTO
import java.time.LocalDateTime

class PageDetailsResponseDTO(

    /**
     * Page Id
     */
    val id: Long,

    /**
     * Page title
     */
    val name: String,

    /**
     * Page content. Markdown string
     */
    val content: String,

    /**
     * Date and time of first page creation
     */
    val dateCreated: LocalDateTime,

    /**
     * Date and time of last page modification
     */
    val dateModified: LocalDateTime,

    /**
     * Id of the parent folder
     */
    val parentFolderId: Long,

    /**
     * Id of scope folder. Might be the same as parentFolderId
     */
    val scopeFolderId: Long
)