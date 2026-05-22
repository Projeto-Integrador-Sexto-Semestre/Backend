package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "alert_types")
class AlertType(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val name: String = "",

    val description: String? = null
)

