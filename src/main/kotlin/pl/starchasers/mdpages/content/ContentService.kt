package pl.starchasers.mdpages.content

import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.content.repository.FolderRepository
import pl.starchasers.mdpages.content.repository.PageRepository

const val DEFAULT_SCOPE_PATH = "/Default"

@Service
class ContentService(private val folderRepository: FolderRepository, private val pageRepository: PageRepository) {

    var globalScope: Folder? = null
        set(value) {
            field = value
        }

    /**
     * @throws ObjectDoesntExistException
     */
    fun getDefaultScope(): Folder =
        globalScope ?: throw ObjectDoesntExistException()


    //TODO validation checks
    fun createFolder(folder: Folder) = folderRepository.save(folder)
}