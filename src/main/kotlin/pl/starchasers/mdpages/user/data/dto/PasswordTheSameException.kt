package pl.starchasers.mdpages.user.data.dto

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class PasswordTheSameException :
    ApplicationException("New password cannot be the same as old password.", HttpStatus.BAD_REQUEST)