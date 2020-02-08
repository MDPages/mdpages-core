package pl.starchasers.mdpages.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pl.starchasers.mdpages.user.UserService
import javax.annotation.PostConstruct

@Component
class Initializer(val userService: UserService) {

    @Value("\${devenv}")
    private val isDevEnv: Boolean = false

    @PostConstruct
    fun initialize() {
        checkRootAccount()
    }

    private fun checkRootAccount() {

        if (userService.findUserByUsername("root") == null) {
            val password = Util.randomString(12)
            userService.createUser("root", password)
            println("Root account not found, creating new one. Username: root Password: $password")
        }
    }
}