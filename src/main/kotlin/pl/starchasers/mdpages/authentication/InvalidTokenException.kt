package pl.starchasers.mdpages.authentication

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class InvalidTokenException : ApplicationException("Invalid token", HttpStatus.UNAUTHORIZED)