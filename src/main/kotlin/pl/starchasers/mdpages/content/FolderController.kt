package pl.starchasers.mdpages.content

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.dto.CreateFolderDTO
import pl.starchasers.mdpages.content.data.dto.FolderIdResponseDTO
import pl.starchasers.mdpages.security.annotation.PathScopeSecured
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.util.BasicResponseDTO

@RequestMapping("/api/content/folder")
@RestController
class FolderController(
    private val contentService: ContentService
){

    @GetMapping("/{folderId}")
    fun getFolder() {

    }

    @GetMapping("/{folderId}/tree")
    fun getFolderTree(){

    }

    @ScopeSecured(PermissionType.WRITE)
    @PutMapping("")
    fun createFolder(@Validated @RequestBody createFolderDTO: CreateFolderDTO): FolderIdResponseDTO {
        return FolderIdResponseDTO(contentService.createFolder(createFolderDTO.name, createFolderDTO.parent))
    }

    /**
     * @param folderId Id of the folder to delete
     */
    @PathScopeSecured(PermissionType.WRITE, pathParameterName = "folderId")
    @DeleteMapping("/{folderId}")
    fun deleteFolder(@PathVariable(name = "folderId") folderId: Long): BasicResponseDTO {
        contentService.deleteFolder(folderId)
        return BasicResponseDTO()
    }
}