package pl.starchasers.mdpages.authentication

import com.sun.istack.NotNull
import pl.starchasers.mdpages.user.data.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @NotNull
    @ManyToOne(targetEntity = User::class, fetch = FetchType.LAZY)
    val user: User,

    @Column(nullable = false, unique = true, updatable = false)
    val token: String,

    @Column(nullable = false, updatable = false)
    val creationDate: LocalDateTime,

    @Column(nullable = false, updatable = false)
    val expirationDate: LocalDateTime
)