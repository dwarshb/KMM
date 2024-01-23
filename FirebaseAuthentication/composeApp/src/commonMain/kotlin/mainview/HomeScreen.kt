package mainview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import authentication.AuthenticationView
import authentication.AuthenticationViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun HomeScreen(mainScreenViewModel: MainScreenViewModel) {
    val navigator = LocalNavigator.currentOrThrow
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                mainScreenViewModel.clearDatabase()
                val authenticationViewModel = AuthenticationViewModel(mainScreenViewModel.getDriver())
                navigator.push(AuthenticationView(authenticationViewModel))
            }
        ) {
            Text("Logout")
        }
        Text("This is Home Screen")
    }
}