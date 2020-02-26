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

    @ScopeSecured(READ)
    @GetMapping("/object/{objectId}")
    fun getContent(@PathVariable objectId: Long) {
        TODO()
    }

    @ScopeSecured(WRITE)
    @PutMapping("/folder")
    fun createFolder(@Validated @RequestBody createFolderDTO: CreateFolderDTO): FolderIdResponseDTO {
        return FolderIdResponseDTO(contentService.createFolder(createFolderDTO.name, createFolderDTO.parent))
    }

    /**
     * @param folderId Id of the folder to delete
     */
    @PathScopeSecured(WRITE, pathParameterName = "folderId")
    @DeleteMapping("/folder/{folderId}")
    fun deleteFolder(@PathVariable(name = "folderId") folderId: Long): BasicResponseDTO {
        contentService.deleteFolder(folderId)
        return BasicResponseDTO()
    }

    @ScopeSecured(WRITE)
    @PutMapping("/page")
    fun createPage(@RequestBody @Validated createPageDTO: CreatePageDTO): PageIdResponseDTO = PageIdResponseDTO(
        contentService.createPage(
            createPageDTO.parentId,
            createPageDTO.title,
            createPageDTO.content
        ).id
    )


    @PathScopeSecured(WRITE, pathParameterName = "pageId")
    @PatchMapping("/page/{pageId}")
    fun updatePage(@PathVariable(name = "pageId") pageId: Long, @RequestBody @Validated updatePageDTO: UpdatePageDTO): BasicResponseDTO {
        contentService.modifyPage(pageId, updatePageDTO.title, updatePageDTO.content)
        return BasicResponseDTO()
    }

    fun movePage() {
        //TODO implement
    }

    @PathScopeSecured(WRITE, pathParameterName = "pageId")
    @DeleteMapping("/page/{pageId}")
    fun deletePage(@PathVariable(name = "pageId") pageId: Long): BasicResponseDTO {
        contentService.deletePage(pageId)
        return BasicResponseDTO()
    }


}