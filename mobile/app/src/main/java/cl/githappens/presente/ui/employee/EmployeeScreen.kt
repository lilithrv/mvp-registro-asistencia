package cl.githappens.presente.ui.employee

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.githappens.presente.domain.model.AttendanceRecord
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
fun EmployeeHomeRoute(
    session: UserSession,
    onLogout: () -> Unit,
    viewModel: EmployeeViewModel = viewModel(
        key = "employee-${session.id}",
        factory = EmployeeViewModel.factory(session)
    )
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    EmployeeScreen(
        session = session,
        state = state,
        onDateSelected = viewModel::selectDate,
        onRetry = viewModel::retry,
        onLogout = onLogout
    )
}

@Composable
private fun EmployeeScreen(
    session: UserSession,
    state: EmployeeUiState,
    onDateSelected: (LocalDate) -> Unit,
    onRetry: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
    ) {
        EmployeeHeader(onLogout)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink)
                .padding(horizontal = 24.dp, vertical = 34.dp)
        ) {
            Text("MI JORNADA", style = MaterialTheme.typography.labelSmall, color = Acid)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Hola,\n${session.name}.",
                style = MaterialTheme.typography.headlineLarge,
                color = White
            )
            session.rut?.let { rut ->
                Spacer(Modifier.height(18.dp))
                Text("RUT  $rut", style = MaterialTheme.typography.labelSmall, color = White)
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text("CONSULTA POR FECHA", style = MaterialTheme.typography.labelSmall, color = Muted)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Revisa tus marcajes",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink
            )
            Spacer(Modifier.height(18.dp))
            DateSelector(
                date = state.selectedDate,
                onDateSelected = onDateSelected
            )
            Spacer(Modifier.height(22.dp))

            when {
                state.isLoading -> LoadingPanel()
                state.error != null -> ErrorPanel(state.error, onRetry)
                state.record != null -> AttendancePanel(state.record)
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
private fun EmployeeHeader(onLogout: () -> Unit) {
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
            colors = ButtonDefaults.buttonColors(
                containerColor = Paper,
                contentColor = Ink
            ),
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) {
            Text("SALIR", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun DateSelector(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val displayFormatter = DateTimeFormatter.ofPattern(
        "EEEE, d 'de' MMMM",
        Locale.forLanguageTag("es-CL")
    )
    val displayDate = date.format(displayFormatter).replaceFirstChar { it.uppercase() }

    Button(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    onDateSelected(LocalDate.of(year, month + 1, day))
                },
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
        colors = ButtonDefaults.buttonColors(
            containerColor = Paper,
            contentColor = Ink
        ),
        border = BorderStroke(1.dp, Ink)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(displayDate, style = MaterialTheme.typography.bodyLarge)
            Text("CAMBIAR", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun LoadingPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
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
        Text("NO SE PUDO MOSTRAR EL DÍA", style = MaterialTheme.typography.labelSmall, color = Ink)
        Spacer(Modifier.height(10.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = Ink)
        Spacer(Modifier.height(16.dp))
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
private fun AttendancePanel(record: AttendanceRecord) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Ink)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Acid)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RESULTADO", style = MaterialTheme.typography.labelSmall, color = Ink)
            Text(
                text = if (record.checkIn != null || record.checkOut != null) "CON MARCAJES" else "SIN MARCAJES",
                style = MaterialTheme.typography.labelSmall,
                color = Ink
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            TimeBlock(
                label = "ENTRADA",
                time = record.checkIn,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .height(128.dp)
                    .size(width = 1.dp, height = 128.dp)
                    .background(Ink)
            )
            TimeBlock(
                label = "SALIDA",
                time = record.checkOut,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TimeBlock(
    label: String,
    time: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
