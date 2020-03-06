package pl.starchasers.mdpages.security.annotation

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@permissionServiceImpl.hasGlobalPermission(T(pl.starchasers.mdpages.security.permission.GlobalPermissionType).ADMIN, principal)")
annotation class IsAdmin