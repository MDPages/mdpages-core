package pl.starchasers.mdpages.content.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class MalformedFolderNameException :
    ApplicationException(
        errorMessage = "Folder name must be between 1 and 32 alphanumeric characters",
        responseStatus = HttpStatus.BAD_REQUEST
    )