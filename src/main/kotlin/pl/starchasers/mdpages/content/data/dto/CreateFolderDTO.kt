package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.security.Securable
import javax.validation.constraints.NotEmpty

class CreateFolderDTO(
    /**
     * New Folder name. Must be between 1 and 32 alphanumeric characters
     */
    @field:NotEmpty
    val name: String,

    /**
     * Id of the parent directory
     */
    val parent: Long
) : Securable {
    override fun getObjectId(): Long = parent
}