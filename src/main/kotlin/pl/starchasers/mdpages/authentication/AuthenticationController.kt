package pl.starchasers.mdpages.authentication

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.starchasers.mdpages.authentication.dto.LoginDTO
import pl.starchasers.mdpages.authentication.dto.TokenDTO
import pl.starchasers.mdpages.authentication.dto.TokenResponseDTO
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.util.ApplicationException
import pl.starchasers.mdpages.util.BasicResponseDTO
import java.security.Principal

@RestController
@RequestMapping("/api/auth/")
class AuthenticationController(private val tokenService: TokenService, private val userService: UserService) {

    @PostMapping("login")
    fun login(@Validated @RequestBody loginDTO: LoginDTO): TokenResponseDTO {
        try {
            return TokenResponseDTO(
                tokenService.issueRefreshToken(userService.getUserFromCredentials(loginDTO.username, loginDTO.password))
            )
        } catch (e: ApplicationException) {
            throw AuthenticationException()
        }
    }

    @PostMapping("logOut")
    fun logOut(principal: Principal): BasicResponseDTO = TODO()

    @PostMapping("getAccessToken")
    fun getAccessToken(@Validated @RequestBody tokenDTO: TokenDTO): TokenResponseDTO =
        TokenResponseDTO(tokenService.issueAccessToken(tokenDTO.token))

    @PostMapping("refreshToken")
    fun refreshToken(@Validated @RequestBody tokenDTO: TokenDTO): TokenResponseDTO =
        TokenResponseDTO(tokenService.refreshRefreshToken(tokenDTO.token))
}