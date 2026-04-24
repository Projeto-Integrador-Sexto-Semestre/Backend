package smarthouse.com.main.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "sensor_history")
class SensorHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val value: String = "", // O valor lido (ex: "25.5")

    val timestamp: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    var sensor: Sensor? = null
)