package cl.githappens.presente.ui.login

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.githappens.presente.domain.model.UserSession
import cl.githappens.presente.ui.theme.Acid
import cl.githappens.presente.ui.theme.ErrorSoft
import cl.githappens.presente.ui.theme.Ink
import cl.githappens.presente.ui.theme.Muted
import cl.githappens.presente.ui.theme.Paper
import cl.githappens.presente.ui.theme.White

@Composable
fun LoginRoute(
    onAuthenticated: (UserSession) -> Unit,
    notice: String? = null,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    LoginScreen(
        state = state,
        notice = notice,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::togglePasswordVisibility,
        onSubmit = { viewModel.login(onAuthenticated) }
    )
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    notice: String? = null,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink)
                .padding(start = 28.dp, top = 58.dp, end = 28.dp, bottom = 34.dp)
        ) {
            BrandMark()
            Spacer(Modifier.height(48.dp))
            Text(
                text = "GESTIÓN DE PERSONAS",
                style = MaterialTheme.typography.labelSmall,
                color = Acid
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Tu jornada,\nclara y simple.",
                style = MaterialTheme.typography.displayLarge,
                color = White
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Accede a tu información laboral desde un solo lugar.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCBCBCB)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 34.dp)
        ) {
            Text(
                text = "BIENVENIDO",
                style = MaterialTheme.typography.labelSmall,
                color = Muted
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Inicia sesión",
                style = MaterialTheme.typography.headlineLarge,
                color = Ink
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Usa las credenciales asignadas por administración.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )

            notice?.let { message ->
                Spacer(Modifier.height(22.dp))
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Acid)
                        .border(1.dp, Ink)
                        .padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink
                )
            }

            state.error?.let { message ->
                Spacer(Modifier.height(22.dp))
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ErrorSoft)
                        .border(1.dp, Ink)
                        .padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink
                )
            }

            Spacer(Modifier.height(26.dp))
            FieldLabel("CORREO ELECTRÓNICO")
            Spacer(Modifier.height(8.dp))
            PresenteTextField(
                value = state.email,
                onValueChange = onEmailChange,
                placeholder = "nombre@empresa.cl",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FieldLabel("CONTRASEÑA")
                Text(
                    text = if (state.isPasswordVisible) "OCULTAR" else "MOSTRAR",
                    modifier = Modifier.clickable(onClick = onTogglePassword),
                    style = MaterialTheme.typography.labelSmall,
                    color = Muted
                )
            }
            Spacer(Modifier.height(8.dp))
            PresenteTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                placeholder = "••••••••",
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSubmit()
                    }
                )
            )

            Spacer(Modifier.height(26.dp))
            Button(
                onClick = onSubmit,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink,
                    contentColor = White,
                    disabledContainerColor = Ink.copy(alpha = 0.72f),
                    disabledContentColor = White
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Acid,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("INGRESAR  →", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(28.dp))
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
private fun BrandMark() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(White)
    ) {
        Text(
            text = "✓",
            modifier = Modifier.align(Alignment.Center),
            color = Ink,
            style = MaterialTheme.typography.headlineSmall
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(6.dp)
                .background(Acid)
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelSmall, color = Ink)
}

@Composable
private fun PresenteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        placeholder = { Text(placeholder, color = Muted) },
        singleLine = true,
        shape = RectangleShape,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Ink,
            unfocusedTextColor = Ink,
            focusedIndicatorColor = Ink,
            unfocusedIndicatorColor = Ink,
            cursorColor = Ink
        )
    )
}
