package cl.githappens.presente.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.githappens.presente.domain.model.UserRole
import cl.githappens.presente.domain.model.UserSession
import cl.githappens.presente.ui.admin.AdminHomeRoute
import cl.githappens.presente.ui.employee.EmployeeHomeRoute
import cl.githappens.presente.ui.login.LoginRoute

@Composable
fun PresenteApp() {
    var session by remember { mutableStateOf<UserSession?>(null) }
    var loginNotice by remember { mutableStateOf<String?>(null) }

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
            LoginRoute(
                notice = loginNotice,
                onAuthenticated = {
                    loginNotice = null
                    session = it
                }
            )
        } else {
            when (activeSession.role) {
                UserRole.ADMIN -> AdminHomeRoute(
                    session = activeSession,
                    onLogout = {
                        loginNotice = null
                        session = null
                    },
                    onSessionExpired = {
                        loginNotice = "Tu sesión terminó. Ingresa nuevamente para continuar."
                        session = null
                    }
                )
                UserRole.EMPLOYEE -> EmployeeHomeRoute(
                    session = activeSession,
                    onLogout = {
                        loginNotice = null
                        session = null
                    },
                    onSessionExpired = {
                        loginNotice = "Tu sesión terminó. Ingresa nuevamente para continuar."
                        session = null
                    }
                )
            }
        }
    }
}
