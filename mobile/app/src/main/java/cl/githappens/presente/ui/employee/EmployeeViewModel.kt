package cl.githappens.presente.ui.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.githappens.presente.data.remote.ApiClient
import cl.githappens.presente.data.repository.AttendanceRepository
import cl.githappens.presente.data.repository.AttendanceResult
import cl.githappens.presente.domain.model.AttendanceRecord
import cl.githappens.presente.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class EmployeeUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val record: AttendanceRecord? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessionExpired: Boolean = false
)

class EmployeeViewModel(
    private val session: UserSession,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    init {
        loadAttendance()
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, error = null, record = null) }
        loadAttendance()
    }

    fun retry() {
        loadAttendance()
    }

    private fun loadAttendance() {
        val date = _uiState.value.selectedDate
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (
                val result = attendanceRepository.getAttendance(
                    token = session.token,
                    date = date.toString()
                )
            ) {
                is AttendanceResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, record = result.record, error = null)
                    }
                }
                is AttendanceResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, record = null, error = result.message)
                    }
                }
                AttendanceResult.SessionExpired -> {
                    _uiState.update { it.copy(isLoading = false, sessionExpired = true) }
                }
            }
        }
    }

    companion object {
        fun factory(session: UserSession): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EmployeeViewModel(
                        session = session,
                        attendanceRepository = AttendanceRepository(ApiClient.attendanceApi)
                    ) as T
                }
            }
        }
    }
}
