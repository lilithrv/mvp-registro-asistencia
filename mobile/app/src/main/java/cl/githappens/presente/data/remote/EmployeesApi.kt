package cl.githappens.presente.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface EmployeesApi {
    @GET("empleados")
    suspend fun getEmployees(
        @Header("Authorization") authorization: String,
        @Query("estado") status: String
    ): EmployeesResponse
}

data class EmployeesResponse(
    val ok: Boolean,
    val result: EmployeesPayload
)

data class EmployeesPayload(
    val estado: String,
    val total: Int,
    val empleados: List<EmployeePayload>
)

data class EmployeePayload(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val rut: String?,
    val estado: String
)
