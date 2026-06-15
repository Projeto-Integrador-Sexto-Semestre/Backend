package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "houses")
class House(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    var address: String = "",

    // Relacionamento: Uma casa pertence a um Usuário
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null
)