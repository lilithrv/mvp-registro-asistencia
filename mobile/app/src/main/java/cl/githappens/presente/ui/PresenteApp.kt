package cl.githappens.presente.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import cl.githappens.presente.domain.model.UserRole
import cl.githappens.presente.domain.model.UserSession
import cl.githappens.presente.ui.login.LoginRoute
import cl.githappens.presente.ui.theme.Acid
import cl.githappens.presente.ui.theme.Ink
import cl.githappens.presente.ui.theme.Muted
import cl.githappens.presente.ui.theme.Paper
import cl.githappens.presente.ui.theme.White

@Composable
fun PresenteApp() {
    var session by remember { mutableStateOf<UserSession?>(null) }

    AnimatedContent(
        targetState = session,
        transitionSpec = {
            (fadeIn(tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                tween(350)
            )).togetherWith(fadeOut(tween(180)))
        },
        label = "session"
    ) { activeSession ->
        if (activeSession == null) {
            LoginRoute(onAuthenticated = { session = it })
        } else {
            RoleHome(
                session = activeSession,
                onLogout = { session = null }
            )
        }
    }
}

@Composable
private fun RoleHome(
    session: UserSession,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .padding(top = 52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
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
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Acid)
                    .border(1.dp, Ink)
            )
        }

        Spacer(Modifier.height(52.dp))
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = if (session.role == UserRole.ADMIN) "PANEL ADMINISTRATIVO" else "MI JORNADA",
                style = MaterialTheme.typography.labelSmall,
                color = Muted
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Hola,\n${session.name}.",
                style = MaterialTheme.typography.headlineLarge,
                color = Ink
            )
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Ink)
                    .padding(20.dp)
            ) {
                Text(
                    text = if (session.role == UserRole.ADMIN) {
                        "Aquí podrás consultar el estado del equipo y sus registros de asistencia."
                    } else {
                        "Aquí podrás consultar tus entradas y salidas por fecha."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Ink
                )
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink,
                    contentColor = White
                )
            ) {
                Text("CERRAR SESIÓN", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
