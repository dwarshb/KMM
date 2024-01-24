import androidx.compose.ui.window.ComposeUIViewController
import com.dwarshb.firebaseauthentication.DriverFactory

fun MainViewController() = ComposeUIViewController {
    val driverFactory = DriverFactory()
    App(driverFactory.createDriver())
}
