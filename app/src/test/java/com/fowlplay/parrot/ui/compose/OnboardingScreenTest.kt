package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fowlplay.parrot.getEditableText
import com.fowlplay.parrot.stubViewModel
import com.fowlplay.parrot.viewmodel.*
import io.mockk.verify
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// required to access final members on androidx.loader.content.ModernAsyncTask
// see: https://github.com/robolectric/robolectric/issues/6593
@Config(instrumentedPackages = ["androidx.loader.content"])
@RunWith(RobolectricTestRunner::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when submit button clicked with empty username does not submit`() {
        val viewModel = stubViewModel()
        composeTestRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_SUBMIT_BUTTON_TEST_ID).performClick()

        verify(exactly = 0) { viewModel.postIntent(any()) }
    }

    // ThemeSelector breaks performClick() on anything below it after 3+ radio options are added
    // Use instrumented tests for now since they work
//    @Test
//    fun `when submit button clicked with valid username submits`() {
//        val viewModel = stubViewModel()
//        composeTestRule.setContent {
//            OnboardingScreen(viewModel = viewModel)
//        }
//
//        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_USERNAME_TEXTFIELD_TEST_ID).performTextInput("foo")
//        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_SUBMIT_BUTTON_TEST_ID).performClick()
//
//        verify { viewModel.postIntent(any()) }
//    }

    @Test
    fun `when username is too long truncates it`() {
        val viewModel = stubViewModel()
        composeTestRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        val longInput = "aReallyLongNameWhichIsWayOverTheCharacterLimit"
        val truncatedInput = "aReallyLongNameWhichIsWay"

        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_USERNAME_TEXTFIELD_TEST_ID).performTextInput(longInput)
        Assert.assertEquals(
            truncatedInput,
            composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_USERNAME_TEXTFIELD_TEST_ID).getEditableText()
        )
    }
}