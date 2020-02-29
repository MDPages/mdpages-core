package pl.starchasers.mdpages.content.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class MalformedPageException : ApplicationException("Malformed page", HttpStatus.BAD_REQUEST)