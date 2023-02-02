package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fowlplay.parrot.viewmodel.AppIntent
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.R

internal const val ONBOARDING_SCREEN_SUBMIT_BUTTON_TEST_ID = "Onboarding Screen Submit Button"
internal const val ONBOARDING_SCREEN_USERNAME_TEXTFIELD_TEST_ID = "Onboarding Screen Username Textfield"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: ParrotViewModel) {
    var usernameText by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            stringResource(R.string.onboarding_screen_main_header),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            stringResource(R.string.onboarding_screen_sub_header),
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            stringResource(R.string.onboarding_screen_username_label),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = usernameText,
            onValueChange = {
                usernameText = it.substring(0, it.length.coerceAtMost(25))
                            },
            label = { Text(stringResource(R.string.onboarding_screen_username_input_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.outlinedTextFieldColors(unfocusedBorderColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag(ONBOARDING_SCREEN_USERNAME_TEXTFIELD_TEST_ID)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text (
            stringResource(R.string.onboarding_screen_theme_label),
            style = MaterialTheme.typography.titleMedium
        )
        ThemeSelector(viewModel)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (usernameText.isNotEmpty())
                    viewModel.postIntent(AppIntent.OnboardingScreen.CreateUser(
                        "@$usernameText"
                    ))
                      },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag(ONBOARDING_SCREEN_SUBMIT_BUTTON_TEST_ID)
        ) {
            Text(stringResource(R.string.onboarding_screen_submit_button))
        }
    }
}