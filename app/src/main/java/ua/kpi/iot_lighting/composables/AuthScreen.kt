package ua.kpi.iot_lighting.composables

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.kpi.iot_lighting.data.AuthRepository
import ua.kpi.iot_lighting.data.AuthValidator
import ua.kpi.iot_lighting.data.UserProfile
import ua.kpi.iot_lighting.ui.theme.AmberLightPrimary
import ua.kpi.iot_lighting.ui.theme.BorderSlate
import ua.kpi.iot_lighting.ui.theme.CanvasBase
import ua.kpi.iot_lighting.ui.theme.CoralAlert
import ua.kpi.iot_lighting.ui.theme.CyanSensor
import ua.kpi.iot_lighting.ui.theme.EmeraldEco
import ua.kpi.iot_lighting.ui.theme.SurfaceCard
import ua.kpi.iot_lighting.ui.theme.SurfaceCardSecondary
import ua.kpi.iot_lighting.ui.theme.TextMuted
import java.util.UUID

enum class AuthMode {
    LOGIN,
    REGISTER
}

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { AuthRepository.getInstance(context) }
    var currentUser by remember { mutableStateOf<UserProfile?>(repository.getActiveSession()) }
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBase)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "Кабінет користувача",
                style = MaterialTheme.typography.titleLarge,
                color = AmberLightPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Автентифікація та персональний профіль доступу до IoT-хабу",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        AnimatedVisibility(visible = errorMessage != null) {
            errorMessage?.let { error ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CoralAlert.copy(alpha = 0.15f))
                        .border(1.dp, CoralAlert, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = error,
                        color = CoralAlert,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        AnimatedVisibility(visible = successMessage != null) {
            successMessage?.let { success ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldEco.copy(alpha = 0.15f))
                        .border(1.dp, EmeraldEco, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = success,
                        color = EmeraldEco,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        currentUser?.let { user ->
            ProfileView(
                user = user,
                onLogoutClick = {
                    repository.clearSession()
                    currentUser = null
                    authMode = AuthMode.LOGIN
                    errorMessage = null
                    successMessage = "Ви вийшли з облікового запису"
                }
            )
        }

        if (currentUser == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                when (authMode) {
                    AuthMode.LOGIN -> {
                        LoginForm(
                            isLoading = isLoading,
                            onLoginClick = { loginOrEmail, password ->
                                errorMessage = null
                                successMessage = null

                                val validation = AuthValidator.validateLogin(loginOrEmail, password)
                                if (!validation.isValid) {
                                    errorMessage = validation.errorMessage ?: "Невірний email або пароль"
                                    return@LoginForm
                                }

                                scope.launch {
                                    isLoading = true
                                    delay(600)
                                    isLoading = false

                                    val authenticatedUser = repository.authenticate(loginOrEmail, password)
                                    if (authenticatedUser != null) {
                                        repository.saveSession(authenticatedUser.id)
                                        currentUser = authenticatedUser
                                        successMessage = "Успішна авторизація в системі!"
                                    } else {
                                        errorMessage = "Невірний email або пароль"
                                    }
                                }
                            },
                            onGoogleSignInClick = {
                                errorMessage = null
                                successMessage = null
                                scope.launch {
                                    isLoading = true
                                    delay(600)
                                    isLoading = false

                                    val defaultUser = repository.getDefaultUser()
                                    repository.saveSession(defaultUser.id)
                                    currentUser = defaultUser
                                    successMessage = "Вхід через Google успішно виконано!"
                                }
                            },
                            onSwitchToRegister = {
                                authMode = AuthMode.REGISTER
                                errorMessage = null
                                successMessage = null
                            }
                        )
                    }

                    AuthMode.REGISTER -> {
                        RegistrationForm(
                            isLoading = isLoading,
                            onRegisterClick = { name, group, email, password, confirmPassword ->
                                errorMessage = null
                                successMessage = null

                                val validation = AuthValidator.validateRegistration(
                                    name = name,
                                    group = group,
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword
                                )
                                if (!validation.isValid) {
                                    errorMessage = validation.errorMessage
                                    return@RegistrationForm
                                }

                                val newUser = UserProfile(
                                    id = UUID.randomUUID().toString(),
                                    name = name.trim(),
                                    group = group.trim(),
                                    email = email.trim(),
                                    password = password,
                                    role = "Інженер IoT-систем",
                                    variant = "13"
                                )

                                scope.launch {
                                    isLoading = true
                                    delay(700)
                                    isLoading = false

                                    val regResult = repository.registerUser(newUser)
                                    regResult.fold(
                                        onSuccess = {
                                            repository.saveSession(newUser.id)
                                            currentUser = newUser
                                            successMessage = "Акаунт успішно створено! Ласкаво просимо, ${newUser.name}."
                                        },
                                        onFailure = { ex ->
                                            errorMessage = ex.message ?: "Користувач із таким Email вже існує"
                                        }
                                    )
                                }
                            },
                            onSwitchToLogin = {
                                authMode = AuthMode.LOGIN
                                errorMessage = null
                                successMessage = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileView(
    user: UserProfile,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AmberLightPrimary.copy(alpha = 0.2f))
                            .border(1.5.dp, AmberLightPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = AmberLightPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldEco.copy(alpha = 0.15f))
                        .border(1.dp, EmeraldEco.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(EmeraldEco)
                        )
                        Text(
                            text = "ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = EmeraldEco,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StudentInfoPill("Група", user.group)
                StudentInfoPill("Варіант", "#${user.variant}")
                StudentInfoPill("Роль", user.role)
            }

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CoralAlert.copy(alpha = 0.5f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceCardSecondary,
                    contentColor = CoralAlert
                )
            ) {
                Text(
                    text = "Вийти з акаунта",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = CoralAlert
                )
            }
        }
    }
}

@Composable
fun LoginForm(
    isLoading: Boolean,
    onLoginClick: (loginOrEmail: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onSwitchToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loginOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Вхід до кабінету",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = loginOrEmail,
            onValueChange = { loginOrEmail = it },
            label = { Text("Email / Логін") },
            placeholder = { Text("student@kpi.ua") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = authFieldColors()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            placeholder = { Text("••••••••") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = authFieldColors()
        )

        Spacer(modifier = Modifier.height(2.dp))

        Button(
            onClick = { onLoginClick(loginOrEmail, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AmberLightPrimary,
                contentColor = CanvasBase
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CanvasBase,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = "Увійти",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderSlate),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = SurfaceCardSecondary,
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = CanvasBase,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = "Увійти через Google",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Немає акаунту?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            TextButton(
                onClick = onSwitchToRegister,
                enabled = !isLoading
            ) {
                Text(
                    text = "Зареєструватися",
                    color = AmberLightPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RegistrationForm(
    isLoading: Boolean,
    onRegisterClick: (name: String, group: String, email: String, password: String, confirmPassword: String) -> Unit,
    onSwitchToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Створення нового акаунта",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("ПІБ") },
            placeholder = { Text("Недяк Олександр") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = authFieldColors()
        )

        OutlinedTextField(
            value = group,
            onValueChange = { group = it },
            label = { Text("Академічна група") },
            placeholder = { Text("ТВ-32") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = authFieldColors()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("student@kpi.ua") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = authFieldColors()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль (мін. 6 символів)") },
            placeholder = { Text("••••••••") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = authFieldColors()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Підтвердження пароля") },
            placeholder = { Text("••••••••") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = authFieldColors()
        )

        Spacer(modifier = Modifier.height(2.dp))

        Button(
            onClick = { onRegisterClick(name, group, email, password, confirmPassword) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AmberLightPrimary,
                contentColor = CanvasBase
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CanvasBase,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = "Створити акаунт",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Вже є акаунт?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            TextButton(
                onClick = onSwitchToLogin,
                enabled = !isLoading
            ) {
                Text(
                    text = "Увійти",
                    color = AmberLightPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StudentInfoPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(SurfaceCardSecondary, RoundedCornerShape(8.dp))
            .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = CyanSensor
            )
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AmberLightPrimary,
    unfocusedBorderColor = BorderSlate,
    focusedLabelColor = AmberLightPrimary,
    unfocusedLabelColor = TextMuted,
    cursorColor = AmberLightPrimary,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
