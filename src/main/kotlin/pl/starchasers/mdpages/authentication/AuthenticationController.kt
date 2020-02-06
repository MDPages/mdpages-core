package pl.starchasers.mdpages.authentication

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth/")
class AuthenticationController(){

    @PostMapping("login")
    fun login(){
        //TODO implement
    }

    @PostMapping("logOut")
    fun logOut(){
        //TODO implement
    }

    @PostMapping("getAccessToken")
    fun getAccessToken(){
        //TODO implement
    }

    @PostMapping("refreshToken")
    fun refreshToken(){
        //TODO implement
    }
}