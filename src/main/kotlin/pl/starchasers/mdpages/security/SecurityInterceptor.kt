package pl.starchasers.mdpages.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.content.ContentService
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.security.annotation.PathScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SecurityInterceptor(
    private val permissionService: PermissionService,
    private val contentService: ContentService
) :
    HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true
        val annotation = handler.getMethodAnnotation(PathScopeSecured::class.java) ?: return true
        val pathVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) as Map<String, String>

        val objectId = pathVariables[annotation.pathParameterName]?.toLongOrNull() ?: throw ObjectDoesntExistException()

        val scope = contentService.findObject(objectId)?.run { scope ?: this } ?: throw ObjectDoesntExistException()
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        if (annotation.value.any { permissionType ->
                !permissionService.hasScopePermission(
                    scope.fullPath,
                    permissionType,
                    authentication?.name?.toLongOrNull()
                )
            }) throw UnauthorizedException()

        return true
    }
}