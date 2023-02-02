package com.fowlplay.parrot.viewmodel

import androidx.paging.PagingSource
import com.fowlplay.parrot.model.ParrotModel
import com.fowlplay.parrot.model.Screech
import kotlinx.coroutines.flow.MutableStateFlow

class StateHandler(
    private val state: MutableStateFlow<State>,
    private var currentSettings: Settings,
    private val model: ParrotModel,
    private val persistentPagingSources: Array<PagingSource<Int, Screech>?>
) {

    suspend fun initHandler() {
        val postLoadScreen = if (currentSettings.username.isEmpty()) {
            ViewState.OnboardingPage
        } else {
            ViewState.Cacophony
        }

        if (!model.loaded.value) {
            updateViewState(ViewState.SplashPage)
            model.loaded.collect { databaseLoaded ->
                if (databaseLoaded) {
                    updateViewState(postLoadScreen)
                }
            }
        } else {
            updateViewState(postLoadScreen)
        }
    }

    suspend fun handleIntent(intent: AppIntent) =
        when (intent) {
            is AppIntent.UpdateTheme -> updateThemeState(intent.theme)
            is AppIntent.BottomBar -> handleBottomBar(intent)
            is AppIntent.OnboardingScreen -> handleOnboardingScreen(intent)
            is AppIntent.ScreechInput -> handleScreechInput(intent)
            is AppIntent.ScreechButton -> handleScreechButton(intent)
        }

    private fun handleBottomBar(intent: AppIntent.BottomBar) =
        when (intent) {
            is AppIntent.BottomBar.AccountHome -> updateViewState(ViewState.AccountPage)
            is AppIntent.BottomBar.Cacophony -> updateViewState(ViewState.Cacophony)
            is AppIntent.BottomBar.About -> updateViewState(ViewState.AboutPage)
            is AppIntent.BottomBar.NewScreech -> updateDisplayScreechInput(true)
        }

    private suspend fun handleOnboardingScreen(intent: AppIntent.OnboardingScreen) =
        when (intent) {
            is AppIntent.OnboardingScreen.CreateUser -> {
                currentSettings = Settings(intent.userName, state.value.theme).also {
                    model.updateSettings(it)
                }

                updateViewState(ViewState.Cacophony)
            }
        }

    private suspend fun handleScreechInput(intent: AppIntent.ScreechInput) =
        when (intent) {
            is AppIntent.ScreechInput.Submit -> {
                model.insertScreech(Screech(currentSettings.username, intent.contents), true)
                updateDisplayScreechInput(false)
            }
            is AppIntent.ScreechInput.Cancel -> updateDisplayScreechInput(false)
        }

    private suspend fun handleScreechButton(intent: AppIntent.ScreechButton) =
        when (intent) {
            is AppIntent.ScreechButton.Favorite -> {
                model.insertScreech(intent.screech, false)
                persistentPagingSources.forEach { it?.invalidate() }
            }
        }

    private fun updateViewState(viewState: ViewState) {
        state.value = state.value.copy(view = viewState)
    }

    private suspend fun updateThemeState(theme: Theme) {
        currentSettings = Settings(currentSettings.username, theme).also {
            model.updateSettings(it)
        }

        state.value = state.value.copy(theme = theme)
    }

    private fun updateDisplayScreechInput(display: Boolean) {
        state.value = state.value.copy(displayScreechInput = display)
    }
}