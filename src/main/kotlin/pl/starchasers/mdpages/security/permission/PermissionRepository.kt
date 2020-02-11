package pl.starchasers.mdpages.security.permission

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.user.data.User

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {
    fun countAllByUserAndScopeAndPermissionType(user: User, scope: Folder, permissionType: PermissionType): Int

}