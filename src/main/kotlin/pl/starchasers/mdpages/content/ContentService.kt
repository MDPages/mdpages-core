package pl.starchasers.mdpages.content

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.MdObject
import pl.starchasers.mdpages.content.data.Page
import pl.starchasers.mdpages.content.exception.*
import pl.starchasers.mdpages.content.repository.FolderRepository
import pl.starchasers.mdpages.content.repository.ObjectRepository
import pl.starchasers.mdpages.content.repository.PageRepository
import pl.starchasers.mdpages.security.permission.PermissionRepository
import pl.starchasers.mdpages.security.permission.PermissionService
import pl.starchasers.mdpages.security.permission.PermissionType
import java.time.LocalDateTime
import javax.transaction.Transactional

const val DEFAULT_SCOPE_PATH = "/Default"
const val MINIMAL_FOLDER_NAME_LENGTH = 1
const val MAXIMAL_FOLDER_NAME_LENGTH = 32
const val MINIMAL_PAGE_NAME_LENGTH = 1
const val MAXIMAL_PAGE_NAME_LENGTH = 64


interface ContentService {
    var globalScope: Folder?

    fun getDefaultScope(): Folder

    fun createFolder(folder: Folder)

    fun createFolder(name: String, parentId: Long): Folder

    fun deleteFolder(id: Long)

    fun createPage(page: Page)

    fun createPage(parentId: Long, title: String, content: String): Page

    fun modifyPage(pageId: Long, title: String, newContent: String)

    fun deletePage(pageId: Long)

    fun deleteObjectRecursive(obj: MdObject)

    fun findObject(id: Long): MdObject?

    fun getObject(id: Long): MdObject

    fun getFolder(id: Long): Folder

    fun findFolder(id: Long): Folder?

    fun getPage(id: Long): Page

    fun findPage(id: Long): Page?

    fun getScopesReadableByUser(userId: Long?): List<Folder>
}

@Service
class ContentServiceImpl(
    private val folderRepository: FolderRepository,
    private val pageRepository: PageRepository,
    private val mdObjectRepository: ObjectRepository,
    private val permissionRepository: PermissionRepository,
    private val permissionService: PermissionService
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

    override fun getFolder(id: Long): Folder = folderRepository.findFirstById(id) ?: throw ObjectDoesntExistException()

    override fun findFolder(id: Long): Folder? = folderRepository.findFirstById(id)

    override fun getPage(id: Long): Page = pageRepository.findFirstById(id) ?: throw ObjectDoesntExistException()

    override fun findPage(id: Long): Page? = pageRepository.findFirstById(id)

    override fun getScopesReadableByUser(userId: Long?): List<Folder> = folderRepository.findAll()
        .filter { permissionService.hasScopePermission(it.fullPath, PermissionType.READ, userId) }


    override fun createFolder(folder: Folder) {
        validateFolderName(folder.name)
        val parent = folder.parent
        folderRepository.save(folder)

        if (parent != null) {
            parent.children.add(folder)
            folderRepository.save(parent)
        }
    }

    @Transactional
    override fun createFolder(name: String, parentId: Long): Folder {
        validateFolderName(name)

        val parentObject: MdObject = getObject(parentId)
        if(parentObject !is Folder) throw InvalidParentException()
        val parentFolder: Folder = parentObject as Folder

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

        return newFolder
    }

    @Transactional
    override fun deleteFolder(id: Long) {
        val toDelete = getFolder(id)
        if (toDelete.isRoot) throw InvalidParentException()
        if (toDelete.children.isNotEmpty()) throw FolderNotEmptyException()

        permissionRepository.deleteAllByScope(toDelete)
        toDelete.parent?.children?.remove(toDelete)
        folderRepository.delete(toDelete)
    }

    @Transactional
    override fun createPage(page: Page) {
        validatePageName(page.name)
        if (page.parent?.children?.any { it.name == page.name } == true) throw ObjectNameTakenException()

        pageRepository.save(page)
        page.parent?.children?.add(page) ?: throw MalformedPageException()
        folderRepository.save(page.parent ?: throw MalformedPageException())
    }

    @Transactional
    override fun createPage(parentId: Long, title: String, content: String): Page {
        validatePageName(title)
        val parent = getFolder(parentId)
        if (parent.children.any { it.name == title }) throw ObjectNameTakenException()

        val page = Page(content, LocalDateTime.now(), LocalDateTime.now(), false, title, parent, parent.scope ?: parent)
        pageRepository.save(page)
        parent.children.add(page)
        return page
    }

    override fun modifyPage(pageId: Long, title: String, newContent: String) {
        validatePageName(title)

        val page = getPage(pageId).apply {
            if (parent?.children?.any { it.name == title } == true) throw ObjectNameTakenException()
            name = title
            content = newContent
            fullPath = (parent?.fullPath ?: "") + "/$name"
        }
        pageRepository.save(page)
    }

    override fun deletePage(pageId: Long) {
        val page = getPage(pageId)
        pageRepository.delete(page)//TODO non destructive deletion
    }

    private fun validatePageName(name: String) {
        if (name.length < MINIMAL_PAGE_NAME_LENGTH || name.length > MAXIMAL_PAGE_NAME_LENGTH) throw MalformedObjectNameException()
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