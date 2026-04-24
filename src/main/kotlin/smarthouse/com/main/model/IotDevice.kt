package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "iot_devices")
class IotDevice(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String = "",

    @ManyToOne
    @JoinColumn(name = "device_type_id", nullable = false)
    var deviceType: DeviceType? = null, // Agora é uma relação!

    val topic: String = "",
    var status: String = "OFF",

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    var room: Room? = null
)