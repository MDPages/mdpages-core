package pl.starchasers.mdpages.content.data

import pl.starchasers.mdpages.content.ObjectType
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class MdObject(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 64, nullable = false)
    val name: String,

    @Column(nullable = false)
    val objectType: ObjectType,

    @ManyToOne(fetch = FetchType.LAZY)
    var parent: Folder?,

    @Column(length = 2048, unique = true, nullable = false)
    val fullPath: String = (parent?.fullPath ?: "") + "/" + name
)