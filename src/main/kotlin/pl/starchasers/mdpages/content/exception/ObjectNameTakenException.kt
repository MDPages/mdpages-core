package pl.starchasers.mdpages.content.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class ObjectNameTakenException : ApplicationException("Object with this name already exists", HttpStatus.BAD_REQUEST)