package pl.starchasers.mdpages.security.annotation

import pl.starchasers.mdpages.security.permission.PermissionType
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class ScopeSecured(vararg val value: PermissionType)