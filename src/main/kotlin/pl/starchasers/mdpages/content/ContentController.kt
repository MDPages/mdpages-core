package pl.starchasers.mdpages.content

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.dto.*
import pl.starchasers.mdpages.security.annotation.PathScopeSecured
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType.READ
import pl.starchasers.mdpages.security.permission.PermissionType.WRITE
import pl.starchasers.mdpages.util.BasicResponseDTO

@RestController
@RequestMapping("/api/content/")
class ContentController(
    val contentService: ContentService
) {

    @PathScopeSecured(READ, pathParameterName = "objectId")
    @GetMapping("/object/{objectId}")
    fun getContent(@PathVariable(name = "objectId") objectId: Long): MdObjectResponseDTO {
        TODO()
    }
}