package pl.starchasers.mdpages.admin

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.starchasers.mdpages.admin.data.CreateUserDTO
import pl.starchasers.mdpages.security.annotation.IsAdmin
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.util.BasicResponseDTO

@RestController
@RequestMapping("/api/admin")
class AdminController(val userService: UserService) {

    @IsAdmin
    @PostMapping("user")
    fun createUser(@Validated @RequestBody createUserDTO: CreateUserDTO): BasicResponseDTO {
        userService.createUser(createUserDTO.username, createUserDTO.password)
        return BasicResponseDTO()
    }
}