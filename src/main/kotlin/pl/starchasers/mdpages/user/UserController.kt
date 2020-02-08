package pl.starchasers.mdpages.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/")
class UserController(private val userService: UserService) {

    @GetMapping("/details")
    fun getUserDetails(): Nothing = TODO()

    @GetMapping("/publicDetails")
    fun getPublicUserDetails(): Nothing = TODO()

}
