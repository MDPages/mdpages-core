package pl.starchasers.mdpages.content.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class ObjectDoesntExistException : ApplicationException("Object doesn't exist", HttpStatus.NOT_FOUND)