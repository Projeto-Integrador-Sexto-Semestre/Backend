package smarthouse.com.main.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val message: String = "",

    val timestamp: LocalDateTime = LocalDateTime.now(),

    var read: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null
)

