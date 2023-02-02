package com.fowlplay.parrot.ui.compose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
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
class BottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when account button clicked intent is account home`() {
        val viewModel = stubViewModel()
        composeTestRule.setContent {
            BottomBar(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(BOTTOM_BAR_ACCOUNT_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.BottomBar.AccountHome) }
    }

    @Test
    fun `when cacophony button clicked intent is cacophony`() {
        val viewModel = stubViewModel()
        composeTestRule.setContent {
            BottomBar(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(BOTTOM_BAR_CACOPHONY_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.BottomBar.Cacophony) }
    }

    @Test
    fun `when about button clicked intent is about`() {
        val viewModel = stubViewModel()
        composeTestRule.setContent {
            BottomBar(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(BOTTOM_BAR_ABOUT_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.BottomBar.About) }
    }

    @Test
    fun `when not on cacophony page does not have new screech button`() {
        val viewModel = stubViewModel().also {
            every { it.state } returns MutableStateFlow(State(ViewState.SplashPage , Theme.BlueYellowMacaw))
        }
        composeTestRule.setContent {
            BottomBar(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(BOTTOM_BAR_NEW_SCREECH_BUTTON_TEST_ID).assertDoesNotExist()
    }

    @Test
    fun `when on cacophony page does has new screech button`() {
        val viewModel = stubViewModel().also {
            every { it.state } returns MutableStateFlow(State(ViewState.Cacophony , Theme.BlueYellowMacaw))
        }
        composeTestRule.setContent {
            BottomBar(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(BOTTOM_BAR_NEW_SCREECH_BUTTON_TEST_ID).assertExists()
    }

    @Test
    fun `when new screech button clicked intent is new screech`() {
        val viewModel = stubViewModel().also {
            every { it.state } returns MutableStateFlow(State(ViewState.Cacophony , Theme.BlueYellowMacaw))
        }
        composeTestRule.setContent {
            BottomBar(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(BOTTOM_BAR_NEW_SCREECH_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.BottomBar.NewScreech) }
    }

}