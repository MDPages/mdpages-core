package pl.starchasers.mdpages.content.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.starchasers.mdpages.content.data.Page

@Repository
interface PageRepository : JpaRepository<Page, Long> {


    fun findFirstById(id: Long): Page?
}