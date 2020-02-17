package pl.starchasers.mdpages.content

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.dto.CreateFolderDTO
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/content/")
class ContentController() {

    @GetMapping("/objects/**")
    fun getContent(request: HttpServletRequest) {
        println(request.servletPath.removePrefix("/api/content/o/"))
        TODO()
    }

    @ScopeSecured(PermissionType.WRITE)
    @PutMapping("/folders")
    fun createFolder(@Validated @RequestBody createFolderDTO: CreateFolderDTO): Boolean {
        return true
    }

    @ScopeSecured(PermissionType.WRITE)
    @DeleteMapping("/f/**")
    fun deleteFolder() {

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