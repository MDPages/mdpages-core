package pl.starchasers.mdpages.user

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.starchasers.mdpages.user.data.dto.RegisterUserDTO
import pl.starchasers.mdpages.util.BasicResponseDTO

@RestController
@RequestMapping("/api/user/")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@Validated @RequestBody registerUserDTO: RegisterUserDTO): BasicResponseDTO { //TODO disable/enable in configuration
        userService.registerUser(registerUserDTO.username, registerUserDTO.password, registerUserDTO.email)
        return BasicResponseDTO()
    }

    @GetMapping("/details")
    fun getUserDetails(): Nothing = TODO()

    @GetMapping("/publicDetails")
    fun getPublicUserDetails(): Nothing = TODO()

}
