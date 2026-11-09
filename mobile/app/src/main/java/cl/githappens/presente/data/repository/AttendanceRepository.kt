package cl.githappens.presente.data.repository

import cl.githappens.presente.data.remote.ApiErrorResponse
import cl.githappens.presente.data.remote.AttendanceApi
import cl.githappens.presente.domain.model.AttendanceRecord
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class AttendanceRepository(
    private val api: AttendanceApi,
    private val gson: Gson = Gson()
) {
    suspend fun getAttendance(
        token: String,
        date: String,
        rut: String? = null
    ): AttendanceResult {
        return try {
            val response = api.getAttendance("Bearer $token", date, rut)
            AttendanceResult.Success(
                AttendanceRecord(
                    rut = response.result.rut,
                    date = response.result.fecha,
                    employeeName = response.result.nombre,
                    checkIn = response.result.entrada,
                    checkOut = response.result.salida
                )
            )
        } catch (error: HttpException) {
            if (error.code() == 401) {
                return AttendanceResult.SessionExpired
            }
            val message = error.response()?.errorBody()?.string()?.let { body ->
                runCatching { gson.fromJson(body, ApiErrorResponse::class.java).result }.getOrNull()
            }
            AttendanceResult.Error(message ?: "No fue posible consultar la asistencia.")
        } catch (_: IOException) {
            AttendanceResult.Error("No se pudo conectar con el servidor.")
        } catch (_: Exception) {
            AttendanceResult.Error("Ocurrió un problema inesperado al consultar la asistencia.")
        }
    }
}

sealed interface AttendanceResult {
    data class Success(val record: AttendanceRecord) : AttendanceResult
    data class Error(val message: String) : AttendanceResult
    data object SessionExpired : AttendanceResult
}
