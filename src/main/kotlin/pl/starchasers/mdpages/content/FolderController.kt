package pl.starchasers.mdpages.content

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.dto.CreateFolderDTO
import pl.starchasers.mdpages.content.data.dto.FolderIdResponseDTO
import pl.starchasers.mdpages.content.data.dto.FolderResponseDTO
import pl.starchasers.mdpages.content.data.dto.ScopeListResponseDTO
import pl.starchasers.mdpages.security.annotation.PathScopeSecured
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.util.BasicResponseDTO

@RequestMapping("/api/content/folder")
@RestController
class FolderController(
    private val contentService: ContentService
) {

    /**
     * Will not load second level subfolders, which means all `children` fields, except root, will be empty.
     * If you need them, use [getFolderTree]
     */
    @PathScopeSecured(PermissionType.READ, pathParameterName = "folderId")
    @GetMapping("/{folderId}")
    fun getFolder(@PathVariable(name = "folderId") folderId: Long): FolderResponseDTO {
        TODO()
    }

    @PathScopeSecured(PermissionType.READ, pathParameterName = "folderId")
    @GetMapping("/{folderId}/tree")
    fun getFolderTree(@PathVariable(name = "folderId") folderId: Long): FolderResponseDTO {
        TODO()
    }

    /**
     * Returns list of scopes readable by current user. Scopes are folders with one difference, they have no parent.
     * Scope id can be used in all /api/content/folder methods, except delete
     * To get scope contents, use [getFolder] or [getFolderTree] methods
     */
    @GetMapping("/scopes")
    fun getScopes(): ScopeListResponseDTO {
        TODO()
    }

    @ScopeSecured(PermissionType.WRITE)
    @PutMapping("")
    fun createFolder(@Validated @RequestBody createFolderDTO: CreateFolderDTO): FolderIdResponseDTO {
        return FolderIdResponseDTO(contentService.createFolder(createFolderDTO.name, createFolderDTO.parent).id)
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