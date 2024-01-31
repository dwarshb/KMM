import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.dwarshb.firebaseauthentication.DriverFactory
import org.w3c.dom.Worker

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        val driver = DriverFactory().createDriver()
        App(driver)
    }
}