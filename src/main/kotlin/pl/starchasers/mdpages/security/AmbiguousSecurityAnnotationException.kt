package pl.starchasers.mdpages.security

import java.lang.RuntimeException

class AmbiguousSecurityAnnotationException : RuntimeException(
    "Multiple path variables detected. Specify pathParameterName field of the PathScopeSecured annotation"
)