package pl.starchasers.mdpages.security.permission

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.user.data.User
import javax.transaction.Transactional

interface PermissionService {
    fun hasGlobalPermission(permissionType: GlobalPermissionType, userId: Long): Boolean

    fun hasGlobalPermission(permissionType: GlobalPermissionType, user: User): Boolean

    fun hasGlobalPermission(permissionType: GlobalPermissionType, userId: String): Boolean

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: Long?): Boolean

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, user: User): Boolean

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: String): Boolean

    fun grantGlobalPermission(permissionType: GlobalPermissionType, user: User): GlobalPermission

    fun grantScopePermission(scope: Folder, permissionType: PermissionType, user: User): Permission

    fun grantScopePermission(
        scope: Folder,
        permissionType: PermissionType,
        permissionTarget: PermissionTarget
    ): Permission

    fun purgeFolderPermissions(folder: Folder)
}

@Service
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository,
    private val permissionCacheService: PermissionCacheService,
    private val globalPermissionRepository: GlobalPermissionRepository
) : PermissionService {

    override fun hasGlobalPermission(permissionType: GlobalPermissionType, userId: Long): Boolean =
        globalPermissionRepository.findAllByUserId(userId).any { it.globalPermissionType == permissionType }


    override fun hasGlobalPermission(permissionType: GlobalPermissionType, user: User): Boolean =
        hasGlobalPermission(permissionType, user.id)

    override fun hasGlobalPermission(permissionType: GlobalPermissionType, userId: String): Boolean =
        userId.toLongOrNull()?.let { hasGlobalPermission(permissionType, it) } ?: false

    override fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: Long?): Boolean =
        permissionCacheService.hasPermissionAnonymous(scopePath, permissionType)
                || permissionCacheService.hasPermissionAuthenticated(scopePath, permissionType)
                || userId?.let { permissionCacheService.hasPermission(scopePath, userId, permissionType) } ?: false

    override fun hasScopePermission(scopePath: String, permissionType: PermissionType, user: User): Boolean =
        hasScopePermission(scopePath, permissionType, user.id)


    override fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: String): Boolean =
        userId.toLongOrNull()?.let { hasScopePermission(scopePath, permissionType, it) }
            ?: permissionCacheService.hasPermissionAnonymous(scopePath, permissionType)

    override fun grantGlobalPermission(permissionType: GlobalPermissionType, user: User): GlobalPermission =
        GlobalPermission(
            0,
            permissionType,
            user
        ).let { globalPermissionRepository.save(it) }

    override fun grantScopePermission(scope: Folder, permissionType: PermissionType, user: User): Permission =
        Permission(
            scope,
            permissionType,
            PermissionTarget.SPECIFIC,
            user
        ).let { permissionRepository.save(it) }

    override fun grantScopePermission(
        scope: Folder,
        permissionType: PermissionType,
        permissionTarget: PermissionTarget
    ): Permission =
        Permission(
            scope,
            permissionType,
            permissionTarget,
            null
        ).let { permissionRepository.save(it) }

    @Transactional
    override fun purgeFolderPermissions(folder: Folder) {
        permissionRepository.deleteAllByScope(folder)
    }
}