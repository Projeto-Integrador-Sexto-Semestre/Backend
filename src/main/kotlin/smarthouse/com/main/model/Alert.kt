package smarthouse.com.main.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "alerts")
class Alert(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

    var message: String = "",

    val timestamp: LocalDateTime = LocalDateTime.now(),

    var acknowledged: Boolean = false,

    @ManyToOne
	@JoinColumn(name = "alert_type_id")
	var alertType: AlertType? = null,

	// ligação opcional a sensor ou dispositivo
    @ManyToOne
	@JoinColumn(name = "sensor_id")
	var sensor: Sensor? = null,

    @ManyToOne
	@JoinColumn(name = "device_id")
	var device: IotDevice? = null
)


