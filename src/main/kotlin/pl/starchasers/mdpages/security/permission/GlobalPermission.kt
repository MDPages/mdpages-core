package pl.starchasers.mdpages.security.permission

import pl.starchasers.mdpages.user.data.User
import javax.persistence.*

@Entity
class GlobalPermission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false, updatable = false)
    val globalPermissionType: GlobalPermissionType,

    @ManyToOne(optional = false)
    val user: User
)