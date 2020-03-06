package pl.starchasers.mdpages.content

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.Page
import pl.starchasers.mdpages.content.data.dto.*
import pl.starchasers.mdpages.security.annotation.PathScopeSecured
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.util.BasicResponseDTO
import java.security.Principal

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
    fun getFolder(@PathVariable(name = "folderId") folderId: Long): FolderResponseDTO =
        mapFolder(contentService.getFolder(folderId))


    @PathScopeSecured(PermissionType.READ, pathParameterName = "folderId")
    @GetMapping("/{folderId}/tree")
    fun getFolderTree(@PathVariable(name = "folderId") folderId: Long): FolderResponseDTO =
        mapFolder(contentService.getFolder(folderId), recursive = true)

    /**
     * Returns list of scopes readable by current user. Scopes are folders with one difference, they have no parent.
     * Scope id can be used in all /api/content/folder methods, except delete
     * To get scope contents, use [getFolder] or [getFolderTree] methods
     */
    @GetMapping("/scopes")
    fun getScopes(principal: Principal?): ScopeListResponseDTO =
        ScopeListResponseDTO(
            contentService.getScopesReadableByUser(principal?.name?.toLongOrNull())
                .map { ScopeResponseDTO(it.id, it.name) }
        )

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

    private fun mapFolder(folder: Folder, recursive: Boolean = false, root: Boolean = true): FolderResponseDTO =
        FolderResponseDTO(
            folder.name,
            folder.id,
            if (recursive || root) folder.children.map { child ->
                when (child.objectType) {
                    MdObjectType.PAGE -> PageBriefResponseDTO(child.name, child.id)
                    MdObjectType.FOLDER -> mapFolder(child as Folder, recursive, false)
                    else -> throw IllegalStateException("Unknown object type ${child.objectType}")
                }
            }.toSet()
            else emptySet()
        )
}