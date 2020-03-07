package pl.starchasers.mdpages.content

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.dto.*
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.security.annotation.PathScopeSecured
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.util.BasicResponseDTO

@RequestMapping("/api/content/page")
@RestController
class PageController(
    private val contentService: ContentService
) {

    /**
     * @param pageId Id of the queried page
     */
    @PathScopeSecured(PermissionType.READ, pathParameterName = "pageId")
    @GetMapping("/{pageId}")
    fun getPage(@PathVariable(name = "pageId") pageId: Long): PageDetailsResponseWrapperDTO =
        contentService.getPage(pageId).run {
            PageDetailsResponseWrapperDTO(
                PageDetailsResponseDTO(
                    id,
                    name,
                    content,
                    created,
                    lastEdited,
                    parent?.id ?: throw ObjectDoesntExistException(),//TODO caused by
                    scope?.id ?: throw ObjectDoesntExistException() //TODO caused by
                )
            )
        }


    @ScopeSecured(PermissionType.WRITE)
    @PutMapping("")
    fun createPage(@RequestBody @Validated createPageDTO: CreatePageDTO): PageIdResponseDTO = PageIdResponseDTO(
        contentService.createPage(
            createPageDTO.parentId,
            createPageDTO.title,
            createPageDTO.content
        ).id
    )

    /**
     * @param pageId Id of modified page
     */
    @PathScopeSecured(PermissionType.WRITE, pathParameterName = "pageId")
    @PatchMapping("/{pageId}")
    fun updatePage(@PathVariable(name = "pageId") pageId: Long, @RequestBody @Validated updatePageDTO: UpdatePageDTO): BasicResponseDTO {
        contentService.modifyPage(pageId, updatePageDTO.title, updatePageDTO.content)
        return BasicResponseDTO()
    }

    fun movePage() {
        //TODO implement
    }

    /**
     * @param pageId Id of page to delete
     */
    @PathScopeSecured(PermissionType.WRITE, pathParameterName = "pageId")
    @DeleteMapping("/{pageId}")
    fun deletePage(@PathVariable(name = "pageId") pageId: Long): BasicResponseDTO {
        contentService.deletePage(pageId)
        return BasicResponseDTO()
    }
}