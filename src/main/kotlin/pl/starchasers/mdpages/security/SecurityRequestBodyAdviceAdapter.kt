package pl.starchasers.mdpages.security

import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter
import pl.starchasers.mdpages.authentication.UnauthorizedException
import pl.starchasers.mdpages.content.ContentService
import pl.starchasers.mdpages.content.exception.ObjectDoesntExistException
import pl.starchasers.mdpages.security.annotation.ScopeSecured
import pl.starchasers.mdpages.security.permission.PermissionService
import java.lang.reflect.Type

@ControllerAdvice
class SecurityRequestBodyAdviceAdapter(val permissionService: PermissionService, val contentService: ContentService) :
    RequestBodyAdviceAdapter() {
    val logger = LoggerFactory.getLogger(SecurityRequestBodyAdviceAdapter::class.java)

    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean = methodParameter.annotatedElement.isAnnotationPresent(ScopeSecured::class.java)

    override fun afterBodyRead(
        body: Any,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any {
        if (body !is Securable) {
            logger.error("Request DTOs must implement Securable interface when using method security annotations")
            throw UnauthorizedException()
        }
        val scopeFolder =
            contentService.findObject(body.getObjectId())?.run { scope ?: this } ?: throw ObjectDoesntExistException()
        val principal: Authentication? = SecurityContextHolder.getContext().authentication


        if (parameter.annotatedElement.getAnnotationsByType(ScopeSecured::class.java).any {
                it.value.any { permissionType ->
                    !permissionService.hasScopePermission(
                        scopeFolder.fullPath,
                        permissionType,
                        principal?.name?.toLongOrNull()
                    )
                }
            }) throw UnauthorizedException()

        return body
    }
}