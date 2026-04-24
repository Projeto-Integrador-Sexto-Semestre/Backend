package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "sensors")
class Sensor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String = "", // Ex: "Sensor de Temperatura Sala"

    val mqttTopic: String = "", // Ex: "casa/sala/temp"

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    var deviceType: DeviceType? = null,

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    var room: Room? = null
)