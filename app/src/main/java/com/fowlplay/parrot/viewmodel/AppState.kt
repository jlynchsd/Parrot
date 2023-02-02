package com.fowlplay.parrot.viewmodel
import com.fowlplay.parrot.R
import org.json.JSONObject

data class State(
    val view: ViewState,
    val theme: Theme,
    val displayScreechInput: Boolean = false,
    val scrollState: HashMap<String, Pair<Int, Int>> = HashMap()
) {
    companion object {
        fun default() = State(ViewState.SplashPage, Theme.BlueYellowMacaw)
    }
}

enum class ViewState {
    SplashPage,
    OnboardingPage,
    Cacophony,
    AccountPage,
    AboutPage
}

enum class Theme {
    BlueYellowMacaw,
    ScarletMacaw,
    Amazon,
    AfricanGrey;

    fun toStyle() =
        when (this) {
            BlueYellowMacaw -> R.style.Theme_Parrot
            ScarletMacaw -> R.style.Theme_Parrot_ScarletMacaw
            Amazon -> R.style.Theme_Parrot_Amazon
            AfricanGrey -> R.style.Theme_Parrot_Grey
        }
}

data class Settings(
    val username: String,
    val theme: Theme
) {
    override fun toString() =
        JSONObject().apply {
            put(USERNAME_KEY, username)
            put(THEME_KEY, theme.name)
        }.toString()

    companion object {
        private const val USERNAME_KEY = "username"
        private const val THEME_KEY = "theme"

        fun fromString(encodedSettings: String) =
            if (encodedSettings.isNotBlank()) {
                JSONObject(encodedSettings).let {
                    Settings(it.getString(USERNAME_KEY), Theme.valueOf(it.getString(THEME_KEY)))
                }
            } else {
                Settings("", Theme.BlueYellowMacaw)
            }

    }
}