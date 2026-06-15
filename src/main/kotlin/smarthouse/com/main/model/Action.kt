package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "actions")
class Action(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String = "",

    // Dispositivo alvo da ação
    @ManyToOne
    @JoinColumn(name = "device_id")
    var device: IotDevice? = null,

    // Comando a ser enviado (ex: "ON", "OFF", "SET:25")
    var command: String = ""
)

