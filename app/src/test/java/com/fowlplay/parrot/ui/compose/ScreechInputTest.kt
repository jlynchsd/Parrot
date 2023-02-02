package com.fowlplay.parrot.ui.compose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.fowlplay.parrot.stubViewModel
import com.fowlplay.parrot.viewmodel.AppIntent
import com.fowlplay.parrot.viewmodel.State
import com.fowlplay.parrot.viewmodel.Theme
import com.fowlplay.parrot.viewmodel.ViewState
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// required to access final members on androidx.loader.content.ModernAsyncTask
// see: https://github.com/robolectric/robolectric/issues/6593
@Config(instrumentedPackages = ["androidx.loader.content"])
@RunWith(RobolectricTestRunner::class)
class ScreechInputTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when screech input is hidden is not visible`() {
        val viewModel = getViewModel(false)
        composeTestRule.setContent {
            ScreechInputWrapper(viewModel = viewModel) {

            }
        }

        composeTestRule.onNodeWithTag(SCREECH_INPUT_SUBMIT_BUTTON_TEST_ID).assertDoesNotExist()
    }

    @Test
    fun `when screech input is shown is visible`() {
        val viewModel = getViewModel(true)
        composeTestRule.setContent {
            ScreechInputWrapper(viewModel = viewModel) {

            }
        }

        composeTestRule.onNodeWithTag(SCREECH_INPUT_SUBMIT_BUTTON_TEST_ID).assertExists()
    }

    @Test
    fun `when submitting with empty body does not publish intent`() {
        val viewModel = getViewModel()
        composeTestRule.setContent {
            ScreechInputWrapper(viewModel = viewModel) {

            }
        }

        composeTestRule.onNodeWithTag(SCREECH_INPUT_SUBMIT_BUTTON_TEST_ID).performClick()

        verify(exactly = 0) { viewModel.postIntent(any()) }
    }

    @Test
    fun `when submitting with valid body publishes new screech intent`() {
        val viewModel = getViewModel()
        val message = "foo"
        composeTestRule.setContent {
            ScreechInputWrapper(viewModel = viewModel) {

            }
        }

        composeTestRule.onNodeWithTag(SCREECH_INPUT_TEXTFIELD_TEST_ID).performTextInput(message)
        composeTestRule.onNodeWithTag(SCREECH_INPUT_SUBMIT_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.ScreechInput.Submit(message)) }
    }

    @Test
    fun `when cancelling publishes cancel screech intent`() {
        val viewModel = getViewModel()
        val message = "foo"
        composeTestRule.setContent {
            ScreechInputWrapper(viewModel = viewModel) {

            }
        }

        composeTestRule.onNodeWithTag(SCREECH_INPUT_TEXTFIELD_TEST_ID).performTextInput(message)
        composeTestRule.onNodeWithTag(SCREECH_INPUT_CANCEL_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.ScreechInput.Cancel) }
    }

    private fun getViewModel(visible: Boolean = true) = stubViewModel().also {
        every { it.state } returns MutableStateFlow(State(ViewState.OnboardingPage, Theme.BlueYellowMacaw, visible))
    }
}