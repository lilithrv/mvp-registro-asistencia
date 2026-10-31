package cl.githappens.presente.ui.admin

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.githappens.presente.domain.model.AttendanceRecord
import cl.githappens.presente.domain.model.Employee
import cl.githappens.presente.domain.model.EmployeeStatus
import cl.githappens.presente.domain.model.UserSession
import cl.githappens.presente.ui.theme.Acid
import cl.githappens.presente.ui.theme.ErrorSoft
import cl.githappens.presente.ui.theme.Ink
import cl.githappens.presente.ui.theme.Muted
import cl.githappens.presente.ui.theme.Paper
import cl.githappens.presente.ui.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminHomeRoute(
    session: UserSession,
    onLogout: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: AdminViewModel = viewModel(
        key = "admin-${session.id}",
        factory = AdminViewModel.factory(session)
    )
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    LaunchedEffect(state.sessionExpired) {
        if (state.sessionExpired) onSessionExpired()
    }
    AdminScreen(
        session = session,
        state = state,
        onStatusSelected = viewModel::selectStatus,
        onEmployeeSelected = viewModel::selectEmployee,
        onDateSelected = viewModel::selectDate,
        onRetryEmployees = viewModel::retryEmployees,
        onRetryAttendance = viewModel::retryAttendance,
        onLogout = onLogout
    )
}

@Composable
private fun AdminScreen(
    session: UserSession,
    state: AdminUiState,
    onStatusSelected: (EmployeeStatus) -> Unit,
    onEmployeeSelected: (Employee) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onRetryEmployees: () -> Unit,
    onRetryAttendance: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
    ) {
        AdminHeader(onLogout)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink)
                .padding(horizontal = 24.dp, vertical = 34.dp)
        ) {
            Text("PANEL ADMINISTRATIVO", style = MaterialTheme.typography.labelSmall, color = Acid)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Equipo y\nasistencia.",
                style = MaterialTheme.typography.headlineLarge,
                color = White
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Sesión de ${session.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = White
            )
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text("TRABAJADORES", style = MaterialTheme.typography.labelSmall, color = Muted)
            Spacer(Modifier.height(10.dp))
            Text("Estado del equipo", style = MaterialTheme.typography.headlineSmall, color = Ink)
            Spacer(Modifier.height(18.dp))
            StatusSelector(state.status, onStatusSelected)
            Spacer(Modifier.height(20.dp))

            when {
                state.isLoadingEmployees -> LoadingPanel()
                state.employeesError != null -> ErrorPanel(state.employeesError, onRetryEmployees)
                state.employees.isEmpty() -> EmptyEmployeesPanel()
                else -> EmployeeList(
                    employees = state.employees,
                    selectedEmployee = state.selectedEmployee,
                    onEmployeeSelected = onEmployeeSelected
                )
            }

            AnimatedVisibility(
                visible = state.selectedEmployee != null,
                enter = fadeIn(tween(220)) + expandVertically(tween(280)),
                exit = fadeOut(tween(120)) + shrinkVertically(tween(180))
            ) {
                state.selectedEmployee?.let { employee ->
                    AttendanceSection(
                        employee = employee,
                        state = state,
                        onDateSelected = onDateSelected,
                        onRetry = onRetryAttendance
                    )
                }
            }

            Spacer(Modifier.height(36.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Ink)
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "DEVELOPED BY  GIT HAPPENS",
                style = MaterialTheme.typography.labelSmall,
                color = Muted
            )
        }
    }
}

@Composable
private fun AdminHeader(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Ink),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = White)
            }
            Text(
                text = "PRESENTE",
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Ink
            )
        }
        Button(
            onClick = onLogout,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Paper, contentColor = Ink),
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) {
            Text("SALIR", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StatusSelector(
    selected: EmployeeStatus,
    onSelected: (EmployeeStatus) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        EmployeeStatus.entries.forEach { status ->
            val isSelected = status == selected
            Button(
                onClick = { onSelected(status) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Ink else Paper,
                    contentColor = if (isSelected) White else Ink
                ),
                border = BorderStroke(1.dp, Ink),
                contentPadding = ButtonDefaults.TextButtonContentPadding
            ) {
                Text(status.label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun EmployeeList(
    employees: List<Employee>,
    selectedEmployee: Employee?,
    onEmployeeSelected: (Employee) -> Unit
) {
    Column(modifier = Modifier.border(1.dp, Ink)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink)
                .padding(horizontal = 16.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("EQUIPO", style = MaterialTheme.typography.labelSmall, color = White)
            Text("${employees.size} RESULTADOS", style = MaterialTheme.typography.labelSmall, color = Acid)
        }
        employees.forEachIndexed { index, employee ->
            val isSelected = selectedEmployee?.id == employee.id
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) Acid else Paper)
                    .clickable { onEmployeeSelected(employee) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = employee.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Ink
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(employee.email, style = MaterialTheme.typography.bodyMedium, color = Muted)
                    }
                    Text(
                        text = if (isSelected) "SELECCIONADO" else "VER",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "RUT  ${employee.rut ?: "NO INFORMADO"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink
                )
            }
            if (index < employees.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Ink)
                )
            }
        }
    }
}

@Composable
private fun AttendanceSection(
    employee: Employee,
    state: AdminUiState,
    onDateSelected: (LocalDate) -> Unit,
    onRetry: () -> Unit
) {
    Column {
        Spacer(Modifier.height(34.dp))
        Text("ASISTENCIA INDIVIDUAL", style = MaterialTheme.typography.labelSmall, color = Muted)
        Spacer(Modifier.height(8.dp))
        Text(employee.fullName, style = MaterialTheme.typography.headlineSmall, color = Ink)
        Spacer(Modifier.height(16.dp))
        AdminDateSelector(state.selectedDate, onDateSelected)
        Spacer(Modifier.height(18.dp))

        when {
            state.isLoadingAttendance -> LoadingPanel()
            state.attendanceError != null -> ErrorPanel(state.attendanceError, onRetry)
            state.attendance != null -> AttendancePanel(state.attendance)
        }
    }
}

@Composable
private fun AdminDateSelector(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern(
        "EEEE, d 'de' MMMM",
        Locale.forLanguageTag("es-CL")
    )
    val label = date.format(formatter).replaceFirstChar { it.uppercase() }

    Button(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day -> onDateSelected(LocalDate.of(year, month + 1, day)) },
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Paper, contentColor = Ink),
        border = BorderStroke(1.dp, Ink)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text("CAMBIAR", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun LoadingPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(1.dp, Ink),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Ink, strokeWidth = 2.dp)
    }
}

@Composable
private fun ErrorPanel(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ErrorSoft)
            .border(1.dp, Ink)
            .padding(18.dp)
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = Ink)
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onRetry,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = White)
        ) {
            Text("REINTENTAR", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EmptyEmployeesPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .border(1.dp, Ink),
        contentAlignment = Alignment.Center
    ) {
        Text("NO HAY TRABAJADORES EN ESTE ESTADO", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun AttendancePanel(record: AttendanceRecord) {
    Column(modifier = Modifier.border(1.dp, Ink)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Acid)
                .padding(horizontal = 16.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RESULTADO", style = MaterialTheme.typography.labelSmall, color = Ink)
            Text(record.date, style = MaterialTheme.typography.labelSmall, color = Ink)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            AdminTimeBlock("ENTRADA", record.checkIn, Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(width = 1.dp, height = 126.dp)
                    .background(Ink)
            )
            AdminTimeBlock("SALIDA", record.checkOut, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AdminTimeBlock(label: String, time: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(18.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Muted)
        Spacer(Modifier.height(14.dp))
        Text(
            text = time?.take(5) ?: "—",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Light,
            color = Ink
        )
        Text(
            text = if (time == null) "Sin registro" else "Hora registrada",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted
        )
    }
}
