package pl.starchasers.mdpages.content

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.content.data.dto.CreatePageDTO
import pl.starchasers.mdpages.content.data.dto.PageDetailsResponseDTO
import pl.starchasers.mdpages.content.data.dto.PageIdResponseDTO
import pl.starchasers.mdpages.content.data.dto.UpdatePageDTO
import pl.starchasers.mdpages.security.annotation.PathScopeSecured
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.util.BasicResponseDTO

@RequestMapping("/api/content/page")
@RestController
class PageController(
    private val contentService: ContentService
) {

    @PathScopeSecured(PermissionType.READ, pathParameterName = "pageId")
    @GetMapping("/{pageId}")
    fun getPage(@PathVariable(name = "pageId") pageId: Long): PageDetailsResponseDTO {
        TODO()
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