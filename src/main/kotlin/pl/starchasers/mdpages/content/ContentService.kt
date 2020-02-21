package pl.starchasers.mdpages.content

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.MdObject
import pl.starchasers.mdpages.content.exception.FolderNotEmptyException
import pl.starchasers.mdpages.content.exception.MalformedFolderNameException
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.content.exception.ObjectNameTakenException
import pl.starchasers.mdpages.content.repository.FolderRepository
import pl.starchasers.mdpages.content.repository.ObjectRepository
import pl.starchasers.mdpages.content.repository.PageRepository
import pl.starchasers.mdpages.security.permission.PermissionRepository
import pl.starchasers.mdpages.security.permission.PermissionService
import javax.transaction.Transactional

const val DEFAULT_SCOPE_PATH = "/Default"
const val MINIMAL_FOLDER_NAME_LENGTH = 1
const val MAXIMAL_FOLDER_NAME_LENGTH = 32


interface ContentService {
    var globalScope: Folder?

    fun getDefaultScope(): Folder

    fun createFolder(folder: Folder)

    fun createFolder(name: String, parentId: Long): Long

    fun deleteFolder(id: Long)

    fun deleteObjectRecursive(obj: MdObject)

    fun findObject(id: Long): MdObject?

    fun getObject(id: Long): MdObject

    fun getFolder(id: Long): Folder

    fun findFolder(id: Long): Folder?
}

@Service
class ContentServiceImpl(
    private val folderRepository: FolderRepository,
    private val pageRepository: PageRepository,
    private val mdObjectRepository: ObjectRepository,
    private val permissionRepository: PermissionRepository
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

    @Transactional
    override fun getFolder(id: Long): Folder = folderRepository.findFirstById(id) ?: throw ObjectDoesntExistException()

    override fun findFolder(id: Long): Folder? = folderRepository.findFirstById(id)

    override fun createFolder(folder: Folder) {
        validateFolderName(folder.name)
        val parent = folder.parent
        folderRepository.save(folder)

        if (parent != null) {
            parent.children.add(folder)
            folderRepository.save(parent)
        }
    }

    override fun createFolder(name: String, parentId: Long): Long {
        validateFolderName(name)
        val parentFolder: Folder = getObject(parentId) as Folder

        //Check name duplicate
        if (mdObjectRepository.findFirstByFullPath(parentFolder.fullPath + "/" + name) != null)
            throw ObjectNameTakenException()


        val newFolder = Folder(
            false,
            mutableSetOf(),
            name = name,
            parent = parentFolder,
            scope = parentFolder.scope ?: parentFolder
        )
        folderRepository.save(newFolder)
        parentFolder.children.add(newFolder)
        folderRepository.save(parentFolder)

        return newFolder.id
    }

    @Transactional
    override fun deleteFolder(id: Long) {
        val toDelete = getFolder(id)
        if(toDelete.isRoot) throw UnauthorizedException()
        if (toDelete.children.isNotEmpty()) throw FolderNotEmptyException()

        permissionRepository.deleteAllByScope(toDelete)
        toDelete.parent?.children?.remove(toDelete)
        folderRepository.delete(toDelete)
    }

    @Transactional
    override fun deleteObjectRecursive(obj: MdObject) {
        if (obj is Folder) {
            obj.children.forEach { deleteObjectRecursive(it) }
            permissionRepository.deleteAllByScope(obj)
        }

        mdObjectRepository.delete(obj)
    }


    private fun validateFolderName(name: String) {
        if (name.any { !it.isLetterOrDigit() } || name.length < MINIMAL_FOLDER_NAME_LENGTH || name.length > MAXIMAL_FOLDER_NAME_LENGTH)
            throw MalformedFolderNameException()
    }
}