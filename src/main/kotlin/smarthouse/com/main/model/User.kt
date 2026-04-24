package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val email: String = "",

    @Column(nullable = false)
    var password: String = "",

    val name: String = "",

    @ManyToOne // Muitos usuários podem ter o mesmo perfil
    @JoinColumn(name = "profile_id")
    var profile: Profile? = null
)