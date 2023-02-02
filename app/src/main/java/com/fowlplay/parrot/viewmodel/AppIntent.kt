package com.fowlplay.parrot.viewmodel

import com.fowlplay.parrot.model.Screech

sealed class AppIntent {

    class UpdateTheme(val theme: Theme): AppIntent() {

        override fun equals(other: Any?) =
            (other is UpdateTheme) && this.theme == other.theme

        override fun hashCode(): Int {
            return theme.hashCode()
        }
    }

    sealed class BottomBar: AppIntent() {
        object AccountHome: BottomBar()
        object Cacophony: BottomBar()
        object About: BottomBar()
        object NewScreech: BottomBar()
    }

    sealed class OnboardingScreen: AppIntent() {
        class CreateUser(val userName: String): OnboardingScreen() {

            override fun equals(other: Any?) =
                (other is CreateUser) && this.userName == other.userName

            override fun hashCode(): Int {
                return userName.hashCode()
            }
        }
    }

    sealed class ScreechInput: AppIntent() {
        class Submit(val contents: String): ScreechInput() {

            override fun equals(other: Any?) =
                (other is Submit) && this.contents == other.contents

            override fun hashCode(): Int {
                return contents.hashCode()
            }
        }
        object Cancel: ScreechInput()
    }

    sealed class ScreechButton: AppIntent() {
        class Favorite(val screech: Screech): ScreechButton() {

            override fun equals(other: Any?) =
                (other is Favorite) && this.screech == other.screech

            override fun hashCode(): Int {
                return screech.hashCode()
            }
        }
    }
}