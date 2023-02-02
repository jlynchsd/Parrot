package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.fowlplay.parrot.R
import com.fowlplay.parrot.ui.theme.unselectedGrey
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.viewmodel.Settings
import com.fowlplay.parrot.viewmodel.Theme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

internal const val ACCOUNT_SCREEN_MAIN_SCREEN_TEST_ID = "Account Screen Main Screen"
internal const val ACCOUNT_SCREEN_USER_SCREECH_SCREEN_TEST_ID = "Account Screen User Screech Screen"
internal const val ACCOUNT_SCREEN_USER_FAVORITES_SCREEN_TEST_ID = "Account Screen User Favorites Screen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun AccountScreen(viewModel: ParrotViewModel) {
    var headerTitle by remember { mutableStateOf("") }
    headerTitle = stringResource(R.string.account_screen_main_title)

    Scaffold(
        topBar = { TopBar(headerTitle) },
        content = { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val pageCount = 3
                val state = rememberPagerState()
                HorizontalPager(count = pageCount, state = state) { page->
                    when (page) {
                        0 -> {
                            headerTitle = getHeaderTitle(state.currentPage)
                            AccountMainScreen(viewModel = viewModel)
                        }
                        1 -> {
                            headerTitle = getHeaderTitle(state.currentPage)
                            AccountScreechScreen(viewModel = viewModel)
                        }
                        2 -> {
                            headerTitle = getHeaderTitle(state.currentPage)
                            AccountFavoriteScreen(viewModel = viewModel)
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                DotsIndicator(
                    totalDots = pageCount,
                    selectedIndex = state.currentPage,
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unSelectedColor = unselectedGrey
                )
            } },
        bottomBar = { BottomBar(viewModel) }
    )
}

@Composable
private fun getHeaderTitle(pageIndex: Int) =
    when (pageIndex) {
        0 -> stringResource(R.string.account_screen_main_title)
        1 -> stringResource(R.string.account_screen_user_screech_title)
        2 -> stringResource(R.string.account_screen_user_favorites_title)
        else -> ""
    }

@Composable
private fun DotsIndicator(
    totalDots : Int,
    selectedIndex : Int,
    selectedColor: Color,
    unSelectedColor: Color,
){

    LazyRow(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()

    ) {

        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.pager_position_dot_size))
                        .clip(CircleShape)
                        .background(selectedColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.pager_position_dot_size))
                        .clip(CircleShape)
                        .background(unSelectedColor)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
private fun AccountMainScreen(viewModel: ParrotViewModel) {
    val settings by viewModel.model.getSettingsFlow().collectAsState(initial = Settings("", Theme.BlueYellowMacaw))

    Column(
        modifier = Modifier
            .fillMaxSize(.95f)
            .testTag(ACCOUNT_SCREEN_MAIN_SCREEN_TEST_ID),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.account_screen_main_username_label),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = settings.username,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(R.string.account_screen_main_theme_label),
            style = MaterialTheme.typography.titleLarge
        )
        ThemeSelector(viewModel)
    }
}

@Composable
private fun AccountScreechScreen(viewModel: ParrotViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.95f)
            .testTag(ACCOUNT_SCREEN_USER_SCREECH_SCREEN_TEST_ID)
    ) {
        if (viewModel.userScreeches.collectAsLazyPagingItems().itemCount == 0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.95f)
            ) {
                Text(text = stringResource(R.string.account_screen_user_screech_placeholder), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Cacophony(viewModel.userScreeches.collectAsLazyPagingItems(), viewModel, "Account Screen User Screeches")
        }
        
    }
}

@Composable
private fun AccountFavoriteScreen(viewModel: ParrotViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.95f)
            .testTag(ACCOUNT_SCREEN_USER_FAVORITES_SCREEN_TEST_ID)
    ) {
        if (viewModel.favoriteScreeches.collectAsLazyPagingItems().itemCount == 0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.95f)
            ) {
                Text(text = stringResource(R.string.account_screen_user_favorites_placeholder), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Cacophony(viewModel.favoriteScreeches.collectAsLazyPagingItems(), viewModel, "Account Screen Favorite Screeches")
        }
    }
}