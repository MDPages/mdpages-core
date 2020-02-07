package pl.starchasers.mdpages.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.user.UserService

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val tokenService: TokenService,
    private val userService: UserService
) : WebSecurityConfigurerAdapter() {

    override fun configure(web: HttpSecurity) {
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.addFilterBefore(
            JwtTokenFilter(tokenService, userService),
            UsernamePasswordAuthenticationFilter::class.java
        )
    }
}