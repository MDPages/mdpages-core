package pl.starchasers.mdpages.content

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.MdObject
import pl.starchasers.mdpages.content.exception.MalformedFolderNameException
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.content.repository.FolderRepository
import pl.starchasers.mdpages.content.repository.ObjectRepository
import pl.starchasers.mdpages.content.repository.PageRepository

const val DEFAULT_SCOPE_PATH = "/Default"
const val MINIMAL_FOLDER_NAME_LENGTH = 1
const val MAXIMAL_FOLDER_NAME_LENGTH = 32


interface ContentService {
    var globalScope: Folder?

    fun getDefaultScope(): Folder

    fun createFolder(folder: Folder)

    fun createFolder(name: String, parentId: Long?): Long

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

    override fun getObject(id: Long): MdObject =
        mdObjectRepository.findFirstById(id) ?: throw ObjectDoesntExistException()

    //TODO validation checks
    override fun createFolder(folder: Folder) {
        validateFolderName(folder.name)
        folderRepository.save(folder)
    }

    override fun createFolder(name: String, parentId: Long?): Long {
        validateFolderName(name)
        val parentFolder = parentId?.let { getObject(it) }

        TODO()
    }

    private fun validateFolderName(name: String) {
        if (name.any { !it.isLetterOrDigit() } || name.length < MINIMAL_FOLDER_NAME_LENGTH || name.length > MAXIMAL_FOLDER_NAME_LENGTH)
            throw MalformedFolderNameException()
    }
}