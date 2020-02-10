package pl.starchasers.mdpages.content

import org.springframework.data.jpa.repository.JpaRepository
import pl.starchasers.mdpages.content.data.MdObject

interface ObjectRepository : JpaRepository<MdObject, Long> {

}