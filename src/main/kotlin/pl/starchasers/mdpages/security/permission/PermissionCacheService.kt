package pl.starchasers.mdpages.security.permission

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Service
import pl.starchasers.mdpages.content.data.Folder
import java.util.concurrent.TimeUnit

const val EMPTY_USER_ID: Long = -1

@Service
class PermissionCacheService(
    val permissionRepository: PermissionRepository
) {
    private val cache = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build<CacheKey, Set<PermissionType>> { getPermissions(it) }


    fun hasPermission(scope: String, userId: Long, permissionType: PermissionType): Boolean =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.SPECIFIC, userId))?.contains(
            permissionType
        ) ?: false

    fun hasPermission(scope: Folder, userId: Long, permissionType: PermissionType): Boolean =
        hasPermission(scope.fullPath, userId, permissionType)

    fun hasPermissionAuthenticated(scope: String, permissionType: PermissionType): Boolean =
        cache.get(
            CacheKey(
                extractScopeName(scope),
                PermissionTarget.AUTHENTICATED,
                EMPTY_USER_ID
            )
        )?.contains(permissionType) ?: false

    fun hasPermissionAuthenticated(scope: Folder, permissionType: PermissionType): Boolean =
        hasPermissionAuthenticated(scope.fullPath, permissionType)

    fun hasPermissionAnonymous(scope: String, permissionType: PermissionType): Boolean =
        cache.get(
            CacheKey(
                extractScopeName(scope),
                PermissionTarget.ANONYMOUS,
                EMPTY_USER_ID
            )
        )?.contains(
            permissionType
        ) ?: false

    fun hasPermissionAnonymous(scope: Folder, permissionType: PermissionType): Boolean =
        hasPermissionAnonymous(scope.fullPath, permissionType)

    fun getPermissions(scope: String, userId: Long): Set<PermissionType> =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.SPECIFIC, userId))
            ?: emptySet()

    fun getPermissions(scope: Folder, userId: Long): Set<PermissionType> =
        getPermissions(scope.fullPath, userId)

    fun getPermissionsAuthenticated(scope: String): Set<PermissionType> =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.AUTHENTICATED, EMPTY_USER_ID))
            ?: emptySet()

    fun getPermissionsAuthenticated(scope: Folder): Set<PermissionType> =
        getPermissionsAuthenticated(scope.fullPath)

    fun getPermissionsAnonymous(scope: String): Set<PermissionType> =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.ANONYMOUS, EMPTY_USER_ID)) ?: emptySet()

    fun getPermissionsAnonymous(scope: Folder): Set<PermissionType> =
        getPermissionsAnonymous(scope.fullPath)

    private fun getPermissions(cacheKey: CacheKey): Set<PermissionType> =
        (if (cacheKey.target == PermissionTarget.SPECIFIC) permissionRepository.getUserPermissions(
            cacheKey.scope,
            cacheKey.userId
        )
        else permissionRepository.getGroupPermissions(
            cacheKey.scope,
            cacheKey.target
        )).map { it.permissionType }.toSet()

    private fun extractScopeName(path: String): String = "/" + path.removePrefix("/").substringBefore("/")

}


private class CacheKey(
    val scope: String,
    val target: PermissionTarget,
    val userId: Long
)