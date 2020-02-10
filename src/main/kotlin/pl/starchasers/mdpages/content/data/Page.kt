package pl.starchasers.mdpages.content.data

import pl.starchasers.mdpages.content.ObjectType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Page(
    @Column(columnDefinition = "TEXT", nullable = false, unique = false)
    val content: String,

    @Column(columnDefinition = "DATETIME", nullable = false, unique = false)
    val created: LocalDateTime,

    @Column(columnDefinition = "DATETIME", nullable = false, unique = false)
    val lastEdited: LocalDateTime,

    name: String,
    parent: Folder?
) : MdObject(name = name, parent = parent, objectType = ObjectType.PAGE)