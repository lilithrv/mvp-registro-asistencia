package cl.githappens.presente.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val ok: Boolean,
    val mensaje: String?,
    val result: LoginPayload
)

data class LoginPayload(
    val token: String,
    val id: Int,
    val nombre: String,
    val rol: String,
    val rut: String?
)

data class ApiErrorResponse(
    val ok: Boolean,
    val result: String?
)
