package pl.starchasers.mdpages.user.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class UsernameTakenException : ApplicationException("This username is already taken.", HttpStatus.BAD_REQUEST)