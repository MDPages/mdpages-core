package pl.starchasers.mdpages.content

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/content/")
class ContentController() {

    @GetMapping("/o/**")
    fun getContent(request: HttpServletRequest) {
        println(request.servletPath.removePrefix("/api/content/o/"))
        TODO()
    }
}