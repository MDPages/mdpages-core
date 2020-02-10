package pl.starchasers.mdpages.user.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class UserNotFoundException : ApplicationException("User not found.", HttpStatus.BAD_REQUEST)