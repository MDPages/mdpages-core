package pl.starchasers.mdpages.security

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 *
 */
interface Securable{
    @JsonIgnore
    fun getObjectId(): Long
}