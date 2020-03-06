package pl.starchasers.mdpages.content.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class InvalidParentException : ApplicationException("Invalid parent folder.", HttpStatus.BAD_REQUEST)