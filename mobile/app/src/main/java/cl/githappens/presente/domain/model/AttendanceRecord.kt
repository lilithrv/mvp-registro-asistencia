package cl.githappens.presente.domain.model

data class AttendanceRecord(
    val rut: String,
    val date: String,
    val employeeName: String,
    val checkIn: String?,
    val checkOut: String?
)
