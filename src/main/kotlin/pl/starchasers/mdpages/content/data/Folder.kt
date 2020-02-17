package pl.starchasers.mdpages.content.data

import pl.starchasers.mdpages.content.ObjectType
import javax.persistence.*


@Entity
class Folder(

    @Column(nullable = false)
    val isRoot: Boolean,

    @OneToMany
    val children: Set<MdObject>,

    name: String,

    parent: Folder?,

    scope: Folder? = null
) : MdObject(
    name = name,
    parent = parent,
    objectType = ObjectType.FOLDER,
    scope = scope
)