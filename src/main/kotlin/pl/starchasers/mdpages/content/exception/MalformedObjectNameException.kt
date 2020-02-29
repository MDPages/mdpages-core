package pl.starchasers.mdpages.content.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class MalformedObjectNameException: ApplicationException("Malformed object name", HttpStatus.BAD_REQUEST)