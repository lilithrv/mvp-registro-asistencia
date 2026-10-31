package cl.githappens.presente.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.githappens.presente.data.remote.ApiClient
import cl.githappens.presente.data.repository.AttendanceRepository
import cl.githappens.presente.data.repository.AttendanceResult
import cl.githappens.presente.data.repository.EmployeesRepository
import cl.githappens.presente.data.repository.EmployeesResult
import cl.githappens.presente.domain.model.AttendanceRecord
import cl.githappens.presente.domain.model.Employee
import cl.githappens.presente.domain.model.EmployeeStatus
import cl.githappens.presente.domain.model.UserSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AdminUiState(
    val status: EmployeeStatus = EmployeeStatus.ACTIVE,
    val employees: List<Employee> = emptyList(),
    val selectedEmployee: Employee? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val attendance: AttendanceRecord? = null,
    val isLoadingEmployees: Boolean = false,
    val isLoadingAttendance: Boolean = false,
    val employeesError: String? = null,
    val attendanceError: String? = null,
    val sessionExpired: Boolean = false
)

class AdminViewModel(
    private val session: UserSession,
    private val employeesRepository: EmployeesRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()
    private var employeesJob: Job? = null
    private var attendanceJob: Job? = null

    init {
        loadEmployees()
    }

    fun selectStatus(status: EmployeeStatus) {
        if (status == _uiState.value.status) return
        attendanceJob?.cancel()
        _uiState.update {
            it.copy(
                status = status,
                selectedEmployee = null,
                attendance = null,
                attendanceError = null
            )
        }
        loadEmployees()
    }

    fun selectEmployee(employee: Employee) {
        _uiState.update {
            it.copy(
                selectedEmployee = employee,
                attendance = null,
                attendanceError = null
            )
        }
        loadAttendance()
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, attendance = null, attendanceError = null) }
        if (_uiState.value.selectedEmployee != null) {
            loadAttendance()
        }
    }

    fun retryEmployees() {
        loadEmployees()
    }

    fun retryAttendance() {
        loadAttendance()
    }

    private fun loadEmployees() {
        employeesJob?.cancel()
        employeesJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isLoadingEmployees = true, employeesError = null, employees = emptyList())
            }
            when (
                val result = employeesRepository.getEmployees(
                    token = session.token,
                    status = _uiState.value.status.apiValue
                )
            ) {
                is EmployeesResult.Success -> {
                    _uiState.update {
                        it.copy(isLoadingEmployees = false, employees = result.employees)
                    }
                }
                is EmployeesResult.Error -> {
                    _uiState.update {
                        it.copy(isLoadingEmployees = false, employeesError = result.message)
                    }
                }
                EmployeesResult.SessionExpired -> {
                    _uiState.update { it.copy(isLoadingEmployees = false, sessionExpired = true) }
                }
            }
        }
    }

    private fun loadAttendance() {
        val employee = _uiState.value.selectedEmployee ?: return
        val rut = employee.rut
        if (rut.isNullOrBlank()) {
            _uiState.update { it.copy(attendanceError = "El trabajador no tiene un RUT asociado.") }
            return
        }

        attendanceJob?.cancel()
        attendanceJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAttendance = true, attendanceError = null) }
            when (
                val result = attendanceRepository.getAttendance(
                    token = session.token,
                    date = _uiState.value.selectedDate.toString(),
                    rut = rut
                )
            ) {
                is AttendanceResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoadingAttendance = false,
                            attendance = result.record,
                            attendanceError = null
                        )
                    }
                }
                is AttendanceResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingAttendance = false,
                            attendance = null,
                            attendanceError = result.message
                        )
                    }
                }
                AttendanceResult.SessionExpired -> {
                    _uiState.update { it.copy(isLoadingAttendance = false, sessionExpired = true) }
                }
            }
        }
    }

    companion object {
        fun factory(session: UserSession): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminViewModel(
                        session = session,
                        employeesRepository = EmployeesRepository(ApiClient.employeesApi),
                        attendanceRepository = AttendanceRepository(ApiClient.attendanceApi)
                    ) as T
                }
            }
        }
    }
}
