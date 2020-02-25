package pl.starchasers.mdpages.content.exception

import org.springframework.http.HttpStatus
import pl.starchasers.mdpages.util.ApplicationException

class FolderNotEmptyException : ApplicationException("Folder is not empty", HttpStatus.BAD_REQUEST)