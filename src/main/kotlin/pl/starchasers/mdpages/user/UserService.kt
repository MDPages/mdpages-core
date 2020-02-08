package pl.starchasers.mdpages.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.stereotype.Service
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.authentication.dto.LoginDTO
import pl.starchasers.mdpages.user.data.User

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {

    /**
     * @throws UserNotFoundException
     */
    fun getUser(id: Long): User = userRepository.getFirstById(id) ?: throw UserNotFoundException()


    fun findUser(id: Long): User? = userRepository.getFirstById(id)

    /**
     * @throws UserNotFoundException
     */
    fun getUserByUsername(username: String): User =
        userRepository.getFirstByUsername(username) ?: throw UserNotFoundException()

    fun findUserByUsername(username: String): User? = userRepository.getFirstByUsername(username)

    /**
     * @throws UserNotFoundException
     */
    fun validateCredentials(username: String, password: String): Boolean =
        passwordEncoder.matches(password, getUserByUsername(username).password)

    fun getUserFromCredentials(username: String, password: String): User =
        getUserByUsername(username).takeIf { passwordEncoder.matches(password, it.password) }
            ?: throw UnauthorizedException()


    fun createUser(username: String, password: String) {
        val user = User(
            0,
            username,
            passwordEncoder.encode(password)
        )

        userRepository.save(user)
    }


}