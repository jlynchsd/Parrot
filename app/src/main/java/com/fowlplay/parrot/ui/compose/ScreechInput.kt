package com.fowlplay.parrot.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fowlplay.parrot.R
import com.fowlplay.parrot.viewmodel.AppIntent
import com.fowlplay.parrot.viewmodel.ParrotViewModel

internal const val SCREECH_INPUT_SUBMIT_BUTTON_TEST_ID = "Screech Input Submit Button"
internal const val SCREECH_INPUT_CANCEL_BUTTON_TEST_ID = "Screech Input Cancel Button"
internal const val SCREECH_INPUT_TEXTFIELD_TEST_ID = "Screech Input Text Field"

@Composable
fun ScreechInputWrapper(viewModel: ParrotViewModel, modifier: Modifier = Modifier, content: @Composable() () -> Unit) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle()

        content()
        AnimatedVisibility(
            state.value.displayScreechInput,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(dimensionResource(R.dimen.screech_input_padding))
        ) {
            ScreechInput(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreechInput(viewModel: ParrotViewModel, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .fillMaxWidth(fraction = .9f)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        var screechText by remember { mutableStateOf("") }

        OutlinedTextField(
            value = screechText,
            onValueChange = { screechText = it.substring(0, it.length.coerceAtMost(140)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.screech_input_height))
                .testTag(SCREECH_INPUT_TEXTFIELD_TEST_ID)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(
                onClick = {
                    if (screechText.isNotEmpty()) {
                        viewModel.postIntent(AppIntent.ScreechInput.Submit(screechText))
                        screechText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag(SCREECH_INPUT_SUBMIT_BUTTON_TEST_ID)
            ) {
                Text(stringResource(R.string.screech_input_submit_button))
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(
                onClick = {
                    viewModel.postIntent(AppIntent.ScreechInput.Cancel)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag(SCREECH_INPUT_CANCEL_BUTTON_TEST_ID)
            ) {
                Text(stringResource(R.string.screech_input_cancel_button))
            }
        }
    }
}