package pl.starchasers.mdpages.content.data.dto

import pl.starchasers.mdpages.security.Securable
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class UpdatePageDTO(
    /**
     * New page title. Must be maximum 64 characters long
     */
    @field:NotEmpty
    val title: String,

    /**
     * New page content. Markdown string
     */
    @field:NotNull
    val content: String

)