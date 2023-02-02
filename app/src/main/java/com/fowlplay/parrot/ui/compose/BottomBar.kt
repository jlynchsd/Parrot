package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fowlplay.parrot.viewmodel.AppIntent
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.viewmodel.ViewState
import com.fowlplay.parrot.R
import com.fowlplay.parrot.viewmodel.State

internal const val BOTTOM_BAR_ACCOUNT_BUTTON_TEST_ID = "Bottom Bar Account Button"
internal const val BOTTOM_BAR_CACOPHONY_BUTTON_TEST_ID = "Bottom Bar Cacophony Button"
internal const val BOTTOM_BAR_ABOUT_BUTTON_TEST_ID = "Bottom Bar About Button"
internal const val BOTTOM_BAR_NEW_SCREECH_BUTTON_TEST_ID = "Bottom Bar New Screech Button"

@Composable
fun BottomBar(viewModel: ParrotViewModel) {
    val currentState by viewModel.state.collectAsState(initial = State.default())
    BottomAppBar {
        IconButton(
            onClick = { viewModel.postIntent(AppIntent.BottomBar.AccountHome) },
            modifier = Modifier.testTag(BOTTOM_BAR_ACCOUNT_BUTTON_TEST_ID)
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = stringResource(R.string.bottom_bar_account_home_button_description),
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(
            onClick = { viewModel.postIntent(AppIntent.BottomBar.Cacophony) },
            modifier = Modifier.testTag(BOTTOM_BAR_CACOPHONY_BUTTON_TEST_ID)
        ) {
            Icon(
                Icons.Filled.Home,
                contentDescription = stringResource(R.string.bottom_bar_cacophony_button_description),
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(
            onClick = { viewModel.postIntent(AppIntent.BottomBar.About) },
            modifier = Modifier.testTag(BOTTOM_BAR_ABOUT_BUTTON_TEST_ID)
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = stringResource(R.string.bottom_bar_about_button_description),
                modifier = Modifier.size(30.dp)
            )
        }

        if (currentState.view == ViewState.Cacophony) {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    onClick = { viewModel.postIntent(AppIntent.BottomBar.NewScreech) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .testTag(BOTTOM_BAR_NEW_SCREECH_BUTTON_TEST_ID)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.bottom_bar_new_screech_description)
                    )
                }
            }
        }
    }
}