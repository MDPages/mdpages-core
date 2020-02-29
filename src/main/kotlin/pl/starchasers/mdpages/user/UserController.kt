package pl.starchasers.mdpages.user

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.security.annotation.IsUser
import pl.starchasers.mdpages.user.data.dto.ChangePasswordDTO
import pl.starchasers.mdpages.user.data.dto.RegisterUserDTO
import pl.starchasers.mdpages.util.BasicResponseDTO
import java.security.Principal
import javax.persistence.Basic
import javax.validation.Valid

@RestController
@RequestMapping("/api/user/")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@Validated @RequestBody registerUserDTO: RegisterUserDTO): BasicResponseDTO { //TODO disable/enable in configuration
        userService.registerUser(registerUserDTO.username, registerUserDTO.password, registerUserDTO.email)
        return BasicResponseDTO()
    }

    @IsUser
    @PostMapping("/changePassword")
    fun changePassword(principal: Principal, @RequestBody @Validated changePasswordDTO: ChangePasswordDTO): BasicResponseDTO {
        userService.changePassword(
            principal.name.toLongOrNull() ?: throw UnauthorizedException(),
            changePasswordDTO.oldPassword,
            changePasswordDTO.newPassword
        )
        return BasicResponseDTO()
    }

    @GetMapping("/details")
    fun getUserDetails(): Nothing = TODO()

    @GetMapping("/publicDetails")
    fun getPublicUserDetails(): Nothing = TODO()

}
