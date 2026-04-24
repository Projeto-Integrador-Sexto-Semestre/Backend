package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val name: String = "", // Ex: "ADMIN", "MORADOR", "VISITANTE"

    val description: String = "",

    val canControlDevices: Boolean = false,
    val canEditStructure: Boolean = false,
    val canViewLogs: Boolean = true
)