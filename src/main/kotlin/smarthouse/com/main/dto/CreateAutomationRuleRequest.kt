package smarthouse.com.main.dto

data class CreateAutomationRuleRequest(
    val name: String,
    val condition: String,
    val enabled: Boolean = true,
    val actionId: Long
)