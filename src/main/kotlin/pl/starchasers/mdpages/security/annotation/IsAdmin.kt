package pl.starchasers.mdpages.security.annotation

import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@permissionService.hasGlobalPermission(T(pl.starchasers.mdpages.security.permission.PermissionType).ADMIN, principal)")
annotation class IsAdmin