package pl.starchasers.mdpages.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.content.ContentService
import pl.starchasers.mdpages.content.DEFAULT_SCOPE_PATH
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.content.repository.FolderRepository
import pl.starchasers.mdpages.security.permission.PermissionService
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.user.UserService
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
class Initializer(
    private val userService: UserService,
    private val contentService: ContentService,
    private val folderRepository: FolderRepository,
    private val permissionService: PermissionService
) {
//    @PersistenceContext
//    private lateinit var entityManager: EntityManager

    @Value("\${devenv}")
    private val isDevEnv: Boolean = false

    @PostConstruct
    fun initialize() {
        initDefaultScope()
        injectDefaultScope()

        initRootAccount()
        verifyRootPermissions()

        folderTest()
    }

    private fun initRootAccount() {

        if (userService.findUserByUsername("root") == null) {
            val password = Util.randomString(12)
            userService.createUser("root", password)
            println("Root account not found, creating new one. Username: root Password: $password")
        }
    }

    private fun initDefaultScope() {
        if (folderRepository.findFirstByFullPath(DEFAULT_SCOPE_PATH) == null)
            contentService.createFolder(
                Folder(
                    true, mutableSetOf(), DEFAULT_SCOPE_PATH.removePrefix("/"), null
                )
            )
//        entityManager.flush()
    }

    private fun injectDefaultScope() {
        contentService.globalScope =
            folderRepository.findFirstByFullPath(DEFAULT_SCOPE_PATH) ?: throw ObjectDoesntExistException()
    }

    private fun verifyRootPermissions() {
        val root = userService.getUserByUsername("root")
        if (!permissionService.hasGlobalPermission(PermissionType.ADMIN, root))
            permissionService.grantGlobalPermission(PermissionType.ADMIN, root)
    }

    private fun folderTest() {
//        val parent = Folder(true, mutableSetOf(), "root", null, null)
//        contentService.createFolder(parent)
//        contentService.createFolder(Folder(false, mutableSetOf(), "testFolder", parent, parent))
//        contentService.deleteObjectRecursive(parent)
    }
}