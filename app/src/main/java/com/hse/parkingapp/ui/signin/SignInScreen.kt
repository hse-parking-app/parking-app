package com.hse.parkingapp.ui.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hse.parkingapp.R
import com.hse.parkingapp.ui.theme.ParkingAppTheme

@Composable
fun SignInScreen(
    onAuthClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background
            )
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 64.dp)
    ) {
        Logo()
        Header()
        Authentication(onAuthClick = onAuthClick)
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.tertiary,
                shape = CircleShape
            )
            .size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(imageVector = ImageVector.vectorResource(R.drawable.parking_logo),
            contentDescription = "Parking App logo")
//        Text(
//            text = stringResource(id = R.string.logo_letter),
//            color = MaterialTheme.colorScheme.primary,
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.titleLarge
//        )
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
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.app_description),
            color = MaterialTheme.colorScheme.onPrimary,
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
    // Provides blue color for text selection and cursor. Leave it or rewrite color schema a bit
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onSecondary,
        backgroundColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
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
                textColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                disabledLabelColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Composable
fun Authentication(
    modifier: Modifier = Modifier,
    onAuthClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 64.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        InputLine(
            value = username,
            onValueChanged = { newUsername -> username = newUsername },
            placeholder = stringResource(id = R.string.username)
        )
        InputLine(
            value = password,
            onValueChanged = { newPassword -> password = newPassword },
            placeholder = stringResource(id = R.string.password),
            isPassword = true
        )
        FilledTonalButton(
            onClick = { onAuthClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(id = R.string.sign_in),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_2)
@Composable
fun SignInScreenPreview() {
    ParkingAppTheme {
        SignInScreen(onAuthClick = {})
    }
}
