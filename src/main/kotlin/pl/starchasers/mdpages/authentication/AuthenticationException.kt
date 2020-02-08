package pl.starchasers.mdpages.authentication

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class AuthenticationException : ApplicationException("Invalid username or password.", HttpStatus.UNAUTHORIZED)