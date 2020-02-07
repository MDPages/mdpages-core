package pl.starchasers.mdpages.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.stereotype.Component
import pl.starchasers.mdpages.user.UserService

@Component
class UsernamePasswordAuthenticationProvider(
    private val userService: UserService,
    private val passwordEncoder: Pbkdf2PasswordEncoder
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication? {
        val username = authentication?.name ?: ""
        val password = authentication?.credentials?.toString() ?: return null

        val user = userService.findUserByUsername(username) ?: return null

        return if (passwordEncoder.matches(password, user.password)) UsernamePasswordAuthenticationToken(
            user.username,
            password)
        else null
    }

    override fun supports(authentication: Class<*>?): Boolean =
        authentication == UsernamePasswordAuthenticationToken::class.java

}