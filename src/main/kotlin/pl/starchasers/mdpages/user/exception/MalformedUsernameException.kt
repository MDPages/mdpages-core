package pl.starchasers.mdpages.user.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class MalformedUsernameException :
    ApplicationException("Username must be alphanumeric and between 3 and 32 characters.", HttpStatus.BAD_REQUEST)