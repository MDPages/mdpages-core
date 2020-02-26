package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.security.Securable
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CreatePageDTO(
    /**
     * Id of parent folder
     */
    @field:NotNull
    val parentId: Long,

    /**
     * Page title. Must be maximum 64 characters.
     */
    @field:NotEmpty
    val title: String,

    /**
     * Page content, markdown string
     */
    @field:NotNull
    val content: String
) : Securable {
    override fun getObjectId(): Long {
        return parentId
    }
}