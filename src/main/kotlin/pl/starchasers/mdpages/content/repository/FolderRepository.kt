package pl.starchasers.mdpages.content.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.starchasers.mdpages.content.data.Folder

@Repository
interface FolderRepository: JpaRepository<Folder, Long>{

    fun findFirstByFullPath(path: String): Folder?
}