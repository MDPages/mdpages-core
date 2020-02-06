package pl.starchasers.mdpages.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.starchasers.mdpages.user.data.User

@Repository
interface UserRepository : JpaRepository<User, Long>{
    fun getFirstById(id: Long): User?

}