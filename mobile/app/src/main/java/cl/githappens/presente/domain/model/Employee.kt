package cl.githappens.presente.domain.model

data class Employee(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val rut: String?,
    val status: String
) {
    val fullName: String
        get() = "$firstName $lastName"
}

enum class EmployeeStatus(val apiValue: String, val label: String) {
    ACTIVE("activo", "ACTIVOS"),
    INACTIVE("inactivo", "INACTIVOS"),
    ALL("todos", "TODOS")
}
