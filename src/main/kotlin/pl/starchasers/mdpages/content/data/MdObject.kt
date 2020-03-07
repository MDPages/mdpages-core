package pl.starchasers.mdpages.content.data

import pl.starchasers.mdpages.content.MdObjectType
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class MdObject(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 64, nullable = false)
    var name: String,

    @Column(nullable = false)
    val objectType: MdObjectType,

    @ManyToOne(fetch = FetchType.LAZY)
    var parent: Folder?,

    @ManyToOne(fetch = FetchType.LAZY)
    var scope: Folder?,

    @Column(length = 2048, unique = true, nullable = false)
    var fullPath: String = (parent?.fullPath ?: "") + "/" + name //TODO what does this do exactly
)