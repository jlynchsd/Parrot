package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fowlplay.parrot.R

@Composable
fun AboutScreen(innerPadding: PaddingValues) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Text(
            text = stringResource(R.string.about_screen_top_header),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Jetpack Compose",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Coroutines",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Paging3",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Dagger2",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Room",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "DataStore",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "'Parrot head icon'",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "https://game-icons.net/1x1/lorc/parrot-head.html",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "by 'Lorc'",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "is licensed under CC BY 3.0",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "https://creativecommons.org/licenses/by/3.0/",
            style = MaterialTheme.typography.bodyMedium,
         )
    }
}