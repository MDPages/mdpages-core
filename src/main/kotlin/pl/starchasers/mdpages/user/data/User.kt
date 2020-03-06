package pl.starchasers.mdpages.user.data

import pl.starchasers.mdpages.security.permission.GlobalPermission
import javax.persistence.*

@Entity(name = "User")
class User(
    @Column(unique = true, length = 32, updatable = false, nullable = false)
    val username: String,

    @Column(length = 160, nullable = false)
    var password: String,

    @Column(length = 64, nullable = true)
    val email: String?,

    @OneToMany(mappedBy = "user")
    val globalPermissions: MutableSet<GlobalPermission>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
)