package smarthouse.com.main.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "event_logs")
class EventLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var eventType: String = "",

    var message: String = "",

    val timestamp: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null
)

