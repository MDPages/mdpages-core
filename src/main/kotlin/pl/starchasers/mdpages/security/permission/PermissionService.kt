package pl.starchasers.mdpages.security.permission

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.content.ContentService
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.user.data.User

interface PermissionService {
    fun hasGlobalPermission(permissionType: PermissionType, userId: Long): Boolean

    fun hasGlobalPermission(permissionType: PermissionType, user: User): Boolean

    fun hasGlobalPermission(permissionType: PermissionType, userId: String): Boolean

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: Long?): Boolean

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, user: User): Boolean

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: String): Boolean

    fun grantGlobalPermission(permissionType: PermissionType, user: User): Permission

    fun grantGlobalPermission(permissionType: PermissionType, permissionTarget: PermissionTarget): Permission

    fun grantScopePermission(scope: Folder, permissionType: PermissionType, user: User): Permission

    fun grantScopePermission(
        scope: Folder,
        permissionType: PermissionType,
        permissionTarget: PermissionTarget
    ): Permission
}

@Service
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository,
    private val contentService: ContentService,
    private val permissionCacheService: PermissionCacheService
) : PermissionService {

    override fun hasGlobalPermission(permissionType: PermissionType, userId: Long): Boolean =
        permissionCacheService.hasPermission(contentService.getDefaultScope(), userId, permissionType)
                || permissionCacheService.hasPermissionAnonymous(contentService.getDefaultScope(), permissionType)
                || permissionCacheService.hasPermissionAuthenticated(contentService.getDefaultScope(), permissionType)


    override fun hasGlobalPermission(permissionType: PermissionType, user: User): Boolean =
        hasGlobalPermission(permissionType, user.id)

    override fun hasGlobalPermission(permissionType: PermissionType, userId: String): Boolean =
        userId.toLongOrNull()?.let { hasGlobalPermission(permissionType, it) }
            ?: permissionCacheService.hasPermissionAnonymous(contentService.getDefaultScope(), permissionType)

    override fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: Long?): Boolean =
        permissionCacheService.hasPermissionAnonymous(scopePath, permissionType)
                || permissionCacheService.hasPermissionAuthenticated(scopePath, permissionType)
                || userId?.let { permissionCacheService.hasPermission(scopePath, userId, permissionType) } ?: false

    override fun hasScopePermission(scopePath: String, permissionType: PermissionType, user: User): Boolean =
        hasScopePermission(scopePath, permissionType, user.id)


    override fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: String): Boolean =
        userId.toLongOrNull()?.let { hasScopePermission(scopePath, permissionType, it) }
            ?: permissionCacheService.hasPermissionAnonymous(scopePath, permissionType)

    override fun grantGlobalPermission(permissionType: PermissionType, user: User): Permission =
        Permission(
            contentService.getDefaultScope(),
            permissionType,
            PermissionTarget.SPECIFIC,
            user
        ).let { permissionRepository.save(it) }

    override fun grantGlobalPermission(permissionType: PermissionType, permissionTarget: PermissionTarget): Permission =
        Permission(
            contentService.getDefaultScope(),
            permissionType,
            permissionTarget,
            null
        ).let { permissionRepository.save(it) }

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
}