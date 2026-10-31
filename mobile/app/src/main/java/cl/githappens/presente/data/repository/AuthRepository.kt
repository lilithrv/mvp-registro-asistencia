package cl.githappens.presente.data.repository

import cl.githappens.presente.data.remote.ApiErrorResponse
import cl.githappens.presente.data.remote.AuthApi
import cl.githappens.presente.data.remote.LoginRequest
import cl.githappens.presente.domain.model.UserRole
import cl.githappens.presente.domain.model.UserSession
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val api: AuthApi,
    private val gson: Gson = Gson()
) {
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val response = api.login(LoginRequest(email.trim(), password))
            val role = UserRole.fromApi(response.result.rol)
                ?: return AuthResult.Error("El perfil recibido no es compatible con la aplicación.")

            AuthResult.Success(
                UserSession(
                    token = response.result.token,
                    id = response.result.id,
                    name = response.result.nombre,
                    role = role,
                    rut = response.result.rut
                )
            )
        } catch (error: HttpException) {
            val message = error.response()?.errorBody()?.string()?.let { body ->
                runCatching { gson.fromJson(body, ApiErrorResponse::class.java).result }.getOrNull()
            }
            AuthResult.Error(message ?: "No fue posible iniciar sesión.")
        } catch (_: IOException) {
            AuthResult.Error("No se pudo conectar con el servidor. Verifica tu conexión e inténtalo nuevamente.")
        } catch (_: Exception) {
            AuthResult.Error("Ocurrió un problema inesperado al iniciar sesión.")
        }
    }
}

sealed interface AuthResult {
    data class Success(val session: UserSession) : AuthResult
    data class Error(val message: String) : AuthResult
}
