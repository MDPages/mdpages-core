package pl.starchasers.mdpages.security.annotation

import org.springframework.security.access.annotation.Secured

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Secured("ROLE_USER")
annotation class IsUser