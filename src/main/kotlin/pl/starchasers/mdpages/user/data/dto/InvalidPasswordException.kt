package pl.starchasers.mdpages.user.data.dto

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class InvalidPasswordException : ApplicationException("Invalid old password", HttpStatus.BAD_REQUEST)