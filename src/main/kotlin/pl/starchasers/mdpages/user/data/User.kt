package pl.starchasers.mdpages.user.data

import com.sun.istack.NotNull
import javax.persistence.*

@Entity(name = "User")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @NotNull
    @Column(name = "username", unique = true, length = 32, updatable = false)
    val username: String,

    @NotNull
    @Column(name = "password", length = 160)
    val password: String
)