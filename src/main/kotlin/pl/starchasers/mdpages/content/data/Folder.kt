package pl.starchasers.mdpages.content.data

import pl.starchasers.mdpages.content.ObjectType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany


@Entity
class Folder(

    @Column(nullable = false)
    val isRoot: Boolean,

    @OneToMany(mappedBy = "parent")
    val children: MutableSet<MdObject>,

    name: String,

    parent: Folder?,

    scope: Folder? = null
) : MdObject(
    name = name,
    parent = parent,
    objectType = ObjectType.FOLDER,
    scope = scope
)