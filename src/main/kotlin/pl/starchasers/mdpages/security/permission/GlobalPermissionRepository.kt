package pl.starchasers.mdpages.security.permission

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GlobalPermissionRepository : JpaRepository<GlobalPermission, Long> {

    @Query(
        """
            from GlobalPermission p
            where p.user.id = :userId
        """
    )
    fun findAllByUserId(userId: Long): List<GlobalPermission>
}