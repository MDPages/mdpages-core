package pl.starchasers.mdpages.security.permission

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.content.ContentService
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.user.data.User

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository,
    private val userService: UserService,
    private val contentService: ContentService,
    private val permissionCacheService: PermissionCacheService
) {

    fun hasGlobalPermission(permissionType: PermissionType, userId: Long): Boolean =
        permissionCacheService.hasPermission(contentService.getDefaultScope(), userId, permissionType)
                || permissionCacheService.hasPermissionAnonymous(contentService.getDefaultScope(), permissionType)
                || permissionCacheService.hasPermissionAuthenticated(contentService.getDefaultScope(), permissionType)


    fun hasGlobalPermission(permissionType: PermissionType, user: User): Boolean =
        hasGlobalPermission(permissionType, user.id)

    fun hasGlobalPermission(permissionType: PermissionType, userId: String): Boolean =
        userId.toLongOrNull()?.let { hasGlobalPermission(permissionType, it) }
            ?: permissionCacheService.hasPermissionAnonymous(contentService.getDefaultScope(), permissionType)

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: Long): Boolean =
        permissionCacheService.hasPermissionAnonymous(scopePath, permissionType)
                || permissionCacheService.hasPermissionAuthenticated(scopePath, permissionType)
                || permissionCacheService.hasPermission(scopePath, userId, permissionType)

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, user: User): Boolean =
        hasScopePermission(scopePath, permissionType, user.id)

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: String) =
        userId.toLongOrNull()?.let { hasScopePermission(scopePath, permissionType, it) }
            ?: permissionCacheService.hasPermissionAnonymous(scopePath, permissionType)

    fun grantGlobalPermission(permissionType: PermissionType, user: User): Permission =
        Permission(
            contentService.getDefaultScope(),
            permissionType,
            PermissionTarget.SPECIFIC,
            user
        ).let { permissionRepository.save(it) }

    fun grantGlobalPermission(permissionType: PermissionType, permissionTarget: PermissionTarget): Permission =
        Permission(
            contentService.getDefaultScope(),
            permissionType,
            permissionTarget,
            null
        ).let { permissionRepository.save(it) }

    fun grantScopedPermission(scope: Folder, permissionType: PermissionType, user: User): Permission =
        Permission(
            scope,
            permissionType,
            PermissionTarget.SPECIFIC,
            user
        ).let { permissionRepository.save(it) }

    fun grantScopedPermission(
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