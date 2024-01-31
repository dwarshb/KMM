package authentication

import mainview.MainScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import firebase.FirebaseDatabase
import firebase.FirebaseUser
import firebase.onCompletion
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mainview.MainScreenViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


class AuthenticationView(viewModel : AuthenticationViewModel) : Screen {
    val authenticationViewModel = viewModel
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var title by remember { mutableStateOf("Login") }
        var signIn by remember { mutableStateOf(true) }
        var emailValid by remember { mutableStateOf(true) }
        var passwordError by remember { mutableStateOf(false) }
        var confirmPasswordError by remember { mutableStateOf(false) }
        var signInText by remember { mutableStateOf("Create a new account") }
        var openDialog by remember { mutableStateOf(false) }
        var openDialogTitle by remember { mutableStateOf("") }
        var openDialogText by remember { mutableStateOf("") }
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource("compose-multiplatform.xml"),
                null,
                modifier = Modifier.size(200.dp).padding(32.dp))


            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = title,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
            )

            OutlinedTextField(
                value = email,
                isError = !emailValid,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                isError = passwordError,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = !signIn,
                enter = fadeIn(initialAlpha = 0.4f),
                exit = fadeOut(animationSpec = tween(250))
            ) {
                OutlinedTextField(
                    value = confirmPassword,
                    isError = confirmPasswordError,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isEmpty())
                        emailValid = false
                    else if (password.isEmpty())
                        passwordError = true

                    when (signIn) {
                        true -> {
                            emailValid = authenticationViewModel.validateEmail(email)
                            if (emailValid) {
                                coroutineScope.launch {
                                    authenticationViewModel.login(
                                        email,
                                        password,
                                        object : onCompletion<String> {
                                            override fun onSuccess(token: String) {
                                                openDialogTitle = "Login Success"
                                                openDialogText = "Token: ${token}"
                                                openDialog = true
                                            }

                                            override fun onError(e: Exception) {
                                                openDialogTitle = "Error"
                                                openDialogText = e.message.toString()
                                                openDialog = true
                                            }
                                        })
                                }
                            }
                        }

                        false -> {
                            var emailValid = authenticationViewModel.validateEmail(email)
                            if (emailValid) {
                                coroutineScope.launch {
                                    authenticationViewModel.signUp(
                                        email,
                                        password,
                                        confirmPassword,
                                        object : onCompletion<String> {
                                            override fun onSuccess(token: String) {
                                                openDialogTitle = "Account Created Successfully"
                                                openDialogText = "Token: ${token}"
                                                openDialog = true
                                            }

                                            override fun onError(e: Exception) {
                                                openDialogTitle = "Error"
                                                openDialogText = e.message.toString()
                                                openDialog = true
                                            }
                                        })
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Next")
            }
            Spacer(modifier = Modifier.height(32.dp))

            ClickableText(
                text = AnnotatedString(signInText),
                onClick = {
                    if (signIn) {
                        signIn = false
                        title = "Sign In"
                        signInText = "Log In to existing Account"
                    } else {
                        signIn = true
                        title = "Login"
                        signInText = "Create a New Account"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            if (openDialog) {

                AlertDialog(
                    onDismissRequest = {
                        openDialog = false
                    },
                    title = {
                        Text(text = openDialogTitle)
                    },
                    text = {
                        Text(openDialogText)
                    },
                    confirmButton = {
                        Button(

                            onClick = {
                                openDialog = false
                                if (openDialogTitle != "Error") {
                                    val mainScreenViewModel = MainScreenViewModel(authenticationViewModel.sqlDriver)
                                    navigator.push(MainScreen(mainScreenViewModel))
                                }

                            }) {
                            Text("Ok")
                        }
                    }
                )
            }
        }
    }
}