package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "device_types")
class DeviceType(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val name: String = "", // Ex: "Lâmpada RGB", "Sensor de Umidade", "Ar Condicionado"

    val manufacturer: String = "", // Ex: "Xiaomi", "Sonoff"

    val unit: String? = null // Ex: "°C", "%", "Lux" (para sensores)
)