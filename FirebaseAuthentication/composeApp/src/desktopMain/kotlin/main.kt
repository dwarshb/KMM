import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dwarshb.firebaseauthentication.DriverFactory

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "FirebaseAuthentication") {
        val driverFactory = DriverFactory()
        App(driverFactory.createDriver())
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
//    App()
}