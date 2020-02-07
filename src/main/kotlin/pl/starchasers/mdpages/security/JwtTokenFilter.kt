package pl.starchasers.mdpages.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.user.UserService
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

private const val ROLE_PREFIX = "ROLE_"

class JwtTokenFilter(
    private val tokenService: TokenService,
    private val userService: UserService
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        val token = (request as HttpServletRequest).getHeader("Authorization")

        try {
            val claims = tokenService.parseToken(token.removePrefix("Bearer "))

            val authorities = mutableListOf<GrantedAuthority>()
            authorities.add(SimpleGrantedAuthority(ROLE_PREFIX + "USER"))

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(claims.subject, null, authorities)
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
        }

        chain.doFilter(request, response)
    }
}