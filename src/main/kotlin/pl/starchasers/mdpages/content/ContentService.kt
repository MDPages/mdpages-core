package pl.starchasers.mdpages.content

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.content.repository.FolderRepository
import pl.starchasers.mdpages.content.repository.PageRepository

const val DEFAULT_SCOPE_PATH = "/Default"

interface ContentService {
    var globalScope: Folder?

    fun getDefaultScope(): Folder
    fun createFolder(folder: Folder): Folder?
}

@Service
class ContentServiceImpl(private val folderRepository: FolderRepository, private val pageRepository: PageRepository) :
    ContentService {

    override var globalScope: Folder? = null
        set(value) {
            field = value
        }

    /**
     * @throws ObjectDoesntExistException
     */
    override fun getDefaultScope(): Folder =
        globalScope ?: throw ObjectDoesntExistException()


    //TODO validation checks
    override fun createFolder(folder: Folder): Folder = folderRepository.save(folder)
}