package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "rooms")
class Room(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String = "", // Ex: "Quarto Principal"

    var type: String = "", // Ex: "QUARTO", "COZINHA", "GARAGEM"

    // Relacionamento: Um cómodo pertence a uma Casa
    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    var house: House? = null
)