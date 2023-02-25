package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fowlplay.parrot.viewmodel.AppIntent
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.viewmodel.Theme

internal fun getThemeSelectorButtonTestId(theme: Theme) =
    "Theme Selector Button " + theme.name

@Composable
fun ThemeSelector(viewModel: ParrotViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val radioOptions = Theme.values().map { it.name }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[state.theme.ordinal]) }

    Column {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text); viewModel.postIntent(
                            AppIntent.UpdateTheme(Theme.valueOf(text))
                        )
                        },
                        role = Role.RadioButton
                    )
                    .testTag(getThemeSelectorButtonTestId(Theme.valueOf(text))),
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(selected = (text == selectedOption), onClick = null)
                Text(
                    text = text.replace(
                        String.format(
                            "%s|%s|%s",
                            "(?<=[A-Z])(?=[A-Z][a-z])",
                            "(?<=[^A-Z])(?=[A-Z])",
                            "(?<=[A-Za-z])(?=[^A-Za-z])"
                        ).toRegex(),
                        " "
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}