package pl.starchasers.mdpages.security.permission

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.user.data.User

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {
    fun countAllByUserAndScopeAndPermissionType(user: User, scope: Folder, permissionType: PermissionType): Int

    @Query(
        """
        select p 
        from Permission p 
        where 
            p.permissionTarget = pl.starchasers.mdpages.security.permission.PermissionTarget.SPECIFIC 
            and p.scope.fullPath = :scope 
            and p.user.id = :userId
        """
    )
    fun getUserPermissions(scope: String, userId: Long): Set<Permission>

    @Query(
        """
            from Permission p
            where
                p.permissionTarget = :permissionTarget
                and p.scope.fullPath = :scope
                and p.user is null
        """
    )
    fun getGroupPermissions(scope: String, permissionTarget: PermissionTarget): Set<Permission>

    fun deleteAllByScope(scope: Folder)

    fun deleteAllByUser(user: User)
}