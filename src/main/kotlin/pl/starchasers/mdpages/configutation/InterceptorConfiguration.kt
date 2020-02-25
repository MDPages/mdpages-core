package pl.starchasers.mdpages.configutation

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pl.starchasers.mdpages.content.ContentService
import pl.starchasers.mdpages.security.SecurityInterceptor
import pl.starchasers.mdpages.security.permission.PermissionService

@Configuration
class InterceptorConfiguration(
    private val permissionService: PermissionService,
    private val contentService: ContentService
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(SecurityInterceptor(permissionService, contentService))
    }
}
