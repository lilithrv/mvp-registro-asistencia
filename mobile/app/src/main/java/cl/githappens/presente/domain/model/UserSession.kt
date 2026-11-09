package cl.githappens.presente.domain.model

data class UserSession(
    val token: String,
    val id: Int,
    val name: String,
    val role: UserRole,
    val rut: String?
)

enum class UserRole {
    ADMIN,
    EMPLOYEE;

    companion object {
        fun fromApi(value: String): UserRole? = when (value.lowercase()) {
            "admin" -> ADMIN
            "empleado" -> EMPLOYEE
            else -> null
        }
    }
}
