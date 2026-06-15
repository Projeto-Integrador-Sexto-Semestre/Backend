package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var email: String = "",

    @Column(nullable = false)
    var password: String = "",

    var name: String = "",

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    var profile: Profile? = null
)