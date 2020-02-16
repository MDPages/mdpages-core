package pl.starchasers.mdpages.security.permission

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Service
import pl.starchasers.mdpages.content.data.Folder
import java.util.concurrent.TimeUnit

const val EMPTY_USER_ID: Long = -1

interface PermissionCacheService {
    fun hasPermission(scope: String, userId: Long, permissionType: PermissionType): Boolean

    fun hasPermission(scope: Folder, userId: Long, permissionType: PermissionType): Boolean

    fun hasPermissionAuthenticated(scope: String, permissionType: PermissionType): Boolean

    fun hasPermissionAuthenticated(scope: Folder, permissionType: PermissionType): Boolean

    fun hasPermissionAnonymous(scope: String, permissionType: PermissionType): Boolean

    fun hasPermissionAnonymous(scope: Folder, permissionType: PermissionType): Boolean

    fun getPermissions(scope: String, userId: Long): Set<PermissionType>

    fun getPermissions(scope: Folder, userId: Long): Set<PermissionType>

    fun getPermissionsAuthenticated(scope: String): Set<PermissionType>

    fun getPermissionsAuthenticated(scope: Folder): Set<PermissionType>

    fun getPermissionsAnonymous(scope: String): Set<PermissionType>

    fun getPermissionsAnonymous(scope: Folder): Set<PermissionType>
}

@Service
class PermissionCacheServiceImpl(
    val permissionRepository: PermissionRepository
) : PermissionCacheService {
    private val cache = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(1000) //TODO configuration
        .build<CacheKey, Set<PermissionType>> { getPermissions(it) }


    override fun hasPermission(scope: String, userId: Long, permissionType: PermissionType): Boolean =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.SPECIFIC, userId))?.contains(
            permissionType
        ) ?: false

    override fun hasPermission(scope: Folder, userId: Long, permissionType: PermissionType): Boolean =
        hasPermission(scope.fullPath, userId, permissionType)

    override fun hasPermissionAuthenticated(scope: String, permissionType: PermissionType): Boolean =
        cache.get(
            CacheKey(
                extractScopeName(scope),
                PermissionTarget.AUTHENTICATED,
                EMPTY_USER_ID
            )
        )?.contains(permissionType) ?: false

    override fun hasPermissionAuthenticated(scope: Folder, permissionType: PermissionType): Boolean =
        hasPermissionAuthenticated(scope.fullPath, permissionType)

    override fun hasPermissionAnonymous(scope: String, permissionType: PermissionType): Boolean =
        cache.get(
            CacheKey(
                extractScopeName(scope),
                PermissionTarget.ANONYMOUS,
                EMPTY_USER_ID
            )
        )?.contains(
            permissionType
        ) ?: false

    override fun hasPermissionAnonymous(scope: Folder, permissionType: PermissionType): Boolean =
        hasPermissionAnonymous(scope.fullPath, permissionType)

    override fun getPermissions(scope: String, userId: Long): Set<PermissionType> =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.SPECIFIC, userId))
            ?: emptySet()

    override fun getPermissions(scope: Folder, userId: Long): Set<PermissionType> =
        getPermissions(scope.fullPath, userId)

    override fun getPermissionsAuthenticated(scope: String): Set<PermissionType> =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.AUTHENTICATED, EMPTY_USER_ID))
            ?: emptySet()

    override fun getPermissionsAuthenticated(scope: Folder): Set<PermissionType> =
        getPermissionsAuthenticated(scope.fullPath)

    override fun getPermissionsAnonymous(scope: String): Set<PermissionType> =
        cache.get(CacheKey(extractScopeName(scope), PermissionTarget.ANONYMOUS, EMPTY_USER_ID)) ?: emptySet()

    override fun getPermissionsAnonymous(scope: Folder): Set<PermissionType> =
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