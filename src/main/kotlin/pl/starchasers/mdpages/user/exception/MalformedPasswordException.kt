package pl.starchasers.mdpages.user.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class MalformedPasswordException :
    ApplicationException("Password must be at least 8 characters long", HttpStatus.BAD_REQUEST)