package pl.starchasers.mdpages.configutation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder

@Configuration
class PasswordEncoderConfiguration{
    @Bean
    fun passwordEncoder(): Pbkdf2PasswordEncoder = Pbkdf2PasswordEncoder()
}
