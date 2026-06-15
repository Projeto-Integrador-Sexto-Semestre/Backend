package smarthouse.com.main.model

import jakarta.persistence.*

@Entity
@Table(name = "automation_rules")
class AutomationRule(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String = "",

    // Expressão ou condição (simples string). Ex: "temperature > 30"
    var condition: String = "",

    var enabled: Boolean = true,

    @ManyToOne
    @JoinColumn(name = "action_id")
    var action: Action? = null
)

