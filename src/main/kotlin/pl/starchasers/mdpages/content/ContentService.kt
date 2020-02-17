package pl.starchasers.mdpages.content

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.MdObject
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.content.repository.FolderRepository
import pl.starchasers.mdpages.content.repository.ObjectRepository
import pl.starchasers.mdpages.content.repository.PageRepository

const val DEFAULT_SCOPE_PATH = "/Default"

interface ContentService {
    var globalScope: Folder?

    fun getDefaultScope(): Folder

    fun createFolder(folder: Folder): Folder?

    fun findObject(id: Long): MdObject?

    fun getObject(id: Long): MdObject
}

@Service
class ContentServiceImpl(
    private val folderRepository: FolderRepository,
    private val pageRepository: PageRepository,
    private val mdObjectRepository: ObjectRepository
) :
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

    override fun findObject(id: Long): MdObject? = mdObjectRepository.findFirstById(id)

    override fun getObject(id: Long): MdObject = mdObjectRepository.findFirstById(id) ?: throw ObjectDoesntExistException()

    //TODO validation checks
    override fun createFolder(folder: Folder): Folder = folderRepository.save(folder)
}