package com.hse.parkingapp.ui.signin

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hse.parkingapp.R
import com.hse.parkingapp.ui.theme.ParkingAppTheme

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    authenticationState: AuthenticationState = AuthenticationState(),
    handleEvent: (event: AuthenticationEvent) -> Unit = {  }
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        Logo()
        Header()
        Authentication(
            username = authenticationState.username,
            onUsernameChanged = { username ->
                handleEvent(AuthenticationEvent.UsernameChanged(username))
            },
            password = authenticationState.password,
            onPasswordChanged = { password ->
                handleEvent(AuthenticationEvent.PasswordChanged(password))
            },
            onAuthenticate = {
                handleEvent(AuthenticationEvent.Authenticate)
            }
        )
        // TODO: think about where to place it (maybe outside main Column)
        if (authenticationState.isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.parking_logo),
            contentDescription = "Parking App logo"
        )
    }
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.app_description),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputLine(
    value: String = "",
    onValueChanged: (String) -> Unit = { },
    placeholder: String = "",
    isPassword: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = { onValueChanged(it) },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun Authentication(
    modifier: Modifier = Modifier,
    username: String? = "",
    onUsernameChanged: (username: String) -> Unit = {  },
    password: String? = "",
    onPasswordChanged: (password: String) -> Unit = {  },
    onAuthenticate: () -> Unit = {  }
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 64.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputLine(
            value = username ?: "",
            onValueChanged = onUsernameChanged,
            placeholder = stringResource(id = R.string.username)
        )
        InputLine(
            value = password ?: "",
            onValueChanged = onPasswordChanged,
            placeholder = stringResource(id = R.string.password),
            isPassword = true
        )
        Button(
            onClick = onAuthenticate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(id = R.string.sign_in),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(name = "Sign In light mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true)
@Preview(name = "Sign In dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true)
@Composable
fun SignInScreenPreview() {
    ParkingAppTheme {
        SignInScreen()
    }
}
