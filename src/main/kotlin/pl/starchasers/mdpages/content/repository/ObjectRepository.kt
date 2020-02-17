package pl.starchasers.mdpages.content.repository

import org.springframework.data.jpa.repository.JpaRepository
import pl.starchasers.mdpages.content.data.MdObject

interface ObjectRepository : JpaRepository<MdObject, Long> {
    fun findFirstById(id: Long): MdObject?
}