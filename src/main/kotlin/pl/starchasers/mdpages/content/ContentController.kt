package pl.starchasers.mdpages.content

import org.springframework.context.annotation.Scope
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.dto.CreateFolderDTO
import pl.starchasers.mdpages.content.data.dto.FolderIdDTO
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.security.permission.PermissionType.*
import pl.starchasers.mdpages.util.BasicResponseDTO
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/content/")
class ContentController() {

    @ScopeSecured(READ)
    @GetMapping("/object/{objectId}")
    fun getContent(@PathVariable objectId: Long) {
        TODO()
    }

    @ScopeSecured(WRITE)
    @PutMapping("/folders")
    fun createFolder(@Validated @RequestBody createFolderDTO: CreateFolderDTO): FolderIdDTO {
        TODO()
    }

    @ScopeSecured(WRITE)
    @DeleteMapping("/f/**")
    fun deleteFolder(): BasicResponseDTO {
        TODO()
    }

    fun createPage() {

    }

    fun updatePage() {

    }

    fun movePage() {

    }

    fun deletePage() {

    }


}