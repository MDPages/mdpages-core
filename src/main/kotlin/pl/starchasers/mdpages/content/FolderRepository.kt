package pl.starchasers.mdpages.content

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.starchasers.mdpages.content.data.Folder

@Repository
interface FolderRepository: JpaRepository<Folder, Long>{

}