package cl.githappens.presente.data.repository

import cl.githappens.presente.data.remote.ApiErrorResponse
import cl.githappens.presente.data.remote.EmployeesApi
import cl.githappens.presente.domain.model.Employee
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class EmployeesRepository(
    private val api: EmployeesApi,
    private val gson: Gson = Gson()
) {
    suspend fun getEmployees(token: String, status: String): EmployeesResult {
        return try {
            val response = api.getEmployees("Bearer $token", status)
            EmployeesResult.Success(
                response.result.empleados.map { employee ->
                    Employee(
                        id = employee.id,
                        firstName = employee.nombre,
                        lastName = employee.apellido,
                        email = employee.email,
                        rut = employee.rut,
                        status = employee.estado
                    )
                }
            )
        } catch (error: HttpException) {
            if (error.code() == 401) {
                return EmployeesResult.SessionExpired
            }
            val message = error.response()?.errorBody()?.string()?.let { body ->
                runCatching { gson.fromJson(body, ApiErrorResponse::class.java).result }.getOrNull()
            }
            EmployeesResult.Error(message ?: "No fue posible obtener los trabajadores.")
        } catch (_: IOException) {
            EmployeesResult.Error("No se pudo conectar con el servidor.")
        } catch (_: Exception) {
            EmployeesResult.Error("Ocurrió un problema inesperado al obtener los trabajadores.")
        }
    }
}

sealed interface EmployeesResult {
    data class Success(val employees: List<Employee>) : EmployeesResult
    data class Error(val message: String) : EmployeesResult
    data object SessionExpired : EmployeesResult
}
