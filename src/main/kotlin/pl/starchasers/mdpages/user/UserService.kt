package pl.starchasers.mdpages.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.user.data.User
import pl.starchasers.mdpages.user.exception.MalformedPasswordException
import pl.starchasers.mdpages.user.exception.MalformedUsernameException
import pl.starchasers.mdpages.user.exception.UserNotFoundException
import pl.starchasers.mdpages.user.exception.UsernameTakenException

const val MIN_USERNAME_LENGTH = 3
const val MAX_USERNAME_LENGTH = 32
const val MIN_PASSWORD_LENGTH = 8
const val MAX_PASSWORD_LENGTH = 64

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
        validateUsername(username)
        validatePassword(password)
        if (findUserByUsername(username) != null) throw UsernameTakenException()

        val user = User(
            username,
            passwordEncoder.encode(password),
            null
        )

        userRepository.save(user)
    }

    fun registerUser(username: String, password: String, email: String) {
        validateUsername(username)
        validatePassword(password)

        if (findUserByUsername(username) != null) throw UsernameTakenException()

        val user = User(username, passwordEncoder.encode(password), email)

        userRepository.save(user)
    }

    private fun validateUsername(username: String) {
        if (!username.all { it.isLetterOrDigit() } || username.length < MIN_USERNAME_LENGTH || username.length > MAX_USERNAME_LENGTH) throw MalformedUsernameException()
    }

    private fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LENGTH || password.length > MAX_PASSWORD_LENGTH) throw MalformedPasswordException()
    }
}