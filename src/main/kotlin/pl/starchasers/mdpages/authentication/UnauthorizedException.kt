package pl.starchasers.mdpages.authentication

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class UnauthorizedException() : ApplicationException("Forbidden", HttpStatus.UNAUTHORIZED)