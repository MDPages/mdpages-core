package pl.starchasers.mdpages.security.permission

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.content.ContentService
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.user.data.User

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository,
    private val userService: UserService,
    private val contentService: ContentService
) {

    fun hasGlobalPermission(permissionType: PermissionType, user: User): Boolean =
        permissionRepository.countAllByUserAndScopeAndPermissionType(
            user,
            contentService.getDefaultScope(),
            permissionType
        ) > 0

    fun hasGlobalPermission(permissionType: PermissionType, userId: Long): Boolean =
        hasGlobalPermission(permissionType, userService.getUser(userId))

    fun hasGlobalPermission(permissionType: PermissionType, userId: String): Boolean =
        userId.toLongOrNull()?.let { hasGlobalPermission(permissionType, it) } ?: false

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, user: User): Boolean = TODO()

    fun hasScopePermission(scopePath: String, permissionType: PermissionType, userId: Long): Boolean =
        hasScopePermission(scopePath, permissionType, userService.getUser(userId))

    fun giveGlobalPermission(permissionType: PermissionType, user: User): Permission =
        Permission(
            contentService.getDefaultScope(),
            permissionType,
            PermissionTarget.SPECIFIC,
            user
        ).let { permissionRepository.save(it) }


//    fun giveScopedUserPermission(permissionType: PermissionType, permissionTarget: PermissionTarget)
}