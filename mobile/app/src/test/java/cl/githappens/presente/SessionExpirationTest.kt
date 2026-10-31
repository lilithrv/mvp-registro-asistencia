package cl.githappens.presente

import cl.githappens.presente.data.remote.AttendanceApi
import cl.githappens.presente.data.remote.AttendanceResponse
import cl.githappens.presente.data.remote.EmployeesApi
import cl.githappens.presente.data.remote.EmployeesResponse
import cl.githappens.presente.data.repository.AttendanceRepository
import cl.githappens.presente.data.repository.AttendanceResult
import cl.githappens.presente.data.repository.EmployeesRepository
import cl.githappens.presente.data.repository.EmployeesResult
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class SessionExpirationTest {
    @Test
    fun attendanceUnauthorizedEndsSession() = runBlocking {
        val repository = AttendanceRepository(AttendanceFailureApi(401, "No autenticado"))

        val result = repository.getAttendance("expired-token", "2026-10-30")

        assertSame(AttendanceResult.SessionExpired, result)
    }

    @Test
    fun employeesUnauthorizedEndsSession() = runBlocking {
        val repository = EmployeesRepository(EmployeesFailureApi(401, "No autenticado"))

        val result = repository.getEmployees("expired-token", "activo")

        assertSame(EmployeesResult.SessionExpired, result)
    }

    @Test
    fun validationErrorKeepsSessionAndShowsApiMessage() = runBlocking {
        val repository = AttendanceRepository(AttendanceFailureApi(400, "Fecha inválida"))

        val result = repository.getAttendance("valid-token", "invalid-date")

        assertEquals(AttendanceResult.Error("Fecha inválida"), result)
    }
}

private class AttendanceFailureApi(
    private val status: Int,
    private val message: String
) : AttendanceApi {
    override suspend fun getAttendance(
        authorization: String,
        date: String,
        rut: String?
    ): AttendanceResponse = throw httpException(status, message)
}

private class EmployeesFailureApi(
    private val status: Int,
    private val message: String
) : EmployeesApi {
    override suspend fun getEmployees(
        authorization: String,
        status: String
    ): EmployeesResponse = throw httpException(this.status, message)
}

private fun httpException(status: Int, message: String): HttpException {
    val body = """{"ok":false,"result":"$message"}"""
        .toResponseBody("application/json".toMediaType())
    return HttpException(Response.error<Any>(status, body))
}
