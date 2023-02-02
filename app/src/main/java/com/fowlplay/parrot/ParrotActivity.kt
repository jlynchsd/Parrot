package com.fowlplay.parrot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.fowlplay.parrot.model.ParrotDataStore
import com.fowlplay.parrot.ui.compose.*
import com.fowlplay.parrot.ui.theme.ParrotTheme
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.viewmodel.State
import com.fowlplay.parrot.viewmodel.ViewState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ParrotActivity : ComponentActivity() {
    @Inject
    lateinit var parrotViewModel: ParrotViewModel

    private var currentState: State? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = loadInitialSettings().theme
        setTheme(theme.toStyle())
        super.onCreate(savedInstanceState)
        (application as ParrotApplication).appComponent
            .activityViewModelComponentBuilder()
            .componentActivity(this)
            .build().inject(this)
        currentState = parrotViewModel.state.value
        setContent {
            ParrotTheme(theme = theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                        val navController = rememberNavController()
                        NavHost(navController, ViewState.SplashPage.name) {
                            composable(ViewState.SplashPage.name) {
                                SplashPage()
                            }
                            composable(ViewState.OnboardingPage.name) {
                                OnboardingScreen(parrotViewModel)
                            }
                            composable(ViewState.Cacophony.name) {
                                Scaffold(
                                    topBar = { TopBar(stringResource(R.string.cacophony_screen_title)) },
                                    content = { innerPadding ->
                                        ScreechInputWrapper(parrotViewModel, modifier = Modifier.padding(innerPadding)) {
                                            Cacophony(parrotViewModel.screeches.collectAsLazyPagingItems(), parrotViewModel, "Home Screen")
                                        } },
                                    bottomBar = { BottomBar(parrotViewModel) }
                                )
                            }
                            composable(ViewState.AccountPage.name) {
                                AccountScreen(parrotViewModel)
                            }
                            composable(ViewState.AboutPage.name) {
                                Scaffold(
                                    topBar = { TopBar(stringResource(R.string.about_screen_title)) },
                                    content = { innerPadding -> AboutScreen(innerPadding) },
                                    bottomBar = { BottomBar(parrotViewModel) }
                                )
                            }
                        }

                        BackHandler {
                            if (!navController.popBackStack()) {
                                finish()
                            }
                            navController.currentBackStackEntry?.destination?.route?.let { route ->
                                parrotViewModel.updateState(parrotViewModel.state.value.copy(view = ViewState.valueOf(route)))
                            }
                        }

                        LaunchedEffect(true) {
                            parrotViewModel.state.collect { newState ->
                                currentState?.let { oldState ->
                                    when {
                                        oldState.theme != newState.theme -> recreate()
                                        oldState.view != newState.view ->
                                            navController.navigate(newState.view.name) {
                                                launchSingleTop = true
                                                navController.currentBackStackEntry?.destination?.route?.let {
                                                    if (SKIP_HISTORY_SCREENS.contains(it)) {
                                                        popUpTo(it) { inclusive = true }
                                                    }
                                                }
                                            }
                                    }
                                }

                                currentState = newState
                            }
                        }
                    }
                }
            }
        }

    private fun loadInitialSettings() = runBlocking {
        ParrotDataStore(this@ParrotActivity.applicationContext).settingsFlow.first()
    }

    private companion object {
        val SKIP_HISTORY_SCREENS = listOf(ViewState.SplashPage.name, ViewState.OnboardingPage.name)
    }
}