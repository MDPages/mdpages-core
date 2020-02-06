package pl.starchasers.mdpages.user

import org.springframework.stereotype.Service
import pl.starchasers.mdpages.user.data.User

@Service
class UserService(val userRepository: UserRepository) {

    /**
     * @throws UserNotFoundException
     */
    fun getUser(id: Long): User {
        return userRepository.getFirstById(id) ?: throw UserNotFoundException()
    }

    fun findUser(id: Long): User? {
        return userRepository.getFirstById(id)
    }

    fun registerUser():Nothing = TODO()


}