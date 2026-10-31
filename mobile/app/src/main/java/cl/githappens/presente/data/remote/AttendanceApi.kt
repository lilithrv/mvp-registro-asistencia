package cl.githappens.presente.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AttendanceApi {
    @GET("asistencia")
    suspend fun getAttendance(
        @Header("Authorization") authorization: String,
        @Query("fecha") date: String,
        @Query("rut") rut: String? = null
    ): AttendanceResponse
}

data class AttendanceResponse(
    val ok: Boolean,
    val result: AttendancePayload
)

data class AttendancePayload(
    val rut: String,
    val fecha: String,
    val nombre: String,
    val entrada: String?,
    val salida: String?
)
