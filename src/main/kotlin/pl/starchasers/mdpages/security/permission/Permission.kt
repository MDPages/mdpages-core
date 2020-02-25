package pl.starchasers.mdpages.security.permission

import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.user.data.User
import javax.persistence.*

@Entity
class Permission(

    @ManyToOne(optional = false)//TODO think about fetchType
    val scope: Folder,

    @Column(unique = false, nullable = false)
    val permissionType: PermissionType,

    @Column()
    val permissionTarget: PermissionTarget,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    val user: User?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
)