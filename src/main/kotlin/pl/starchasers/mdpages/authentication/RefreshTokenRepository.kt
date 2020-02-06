package pl.starchasers.mdpages.authentication

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.starchasers.mdpages.user.data.User

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun getFirstById(id: Long): RefreshToken

    @Query("select t from RefreshToken t where t.id = :token and t.user = :user and t.expirationDate < sysdate")
    fun findFirstByTokenAndUser(@Param("token") token: String, @Param("user") user: User): RefreshToken?

    fun deleteAllByUser(user: User)
}