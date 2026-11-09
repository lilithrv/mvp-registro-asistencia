package cl.githappens.presente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.githappens.presente.ui.PresenteApp
import cl.githappens.presente.ui.theme.PresenteMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PresenteMobileTheme {
                PresenteApp()
            }
        }
    }
}
