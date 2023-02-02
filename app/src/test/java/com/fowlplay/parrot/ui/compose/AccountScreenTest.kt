package com.fowlplay.parrot.ui.compose

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fowlplay.parrot.stubViewModel
import com.fowlplay.parrot.viewmodel.Settings
import com.fowlplay.parrot.viewmodel.Theme
import io.mockk.coEvery
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
class AccountScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when swiping through account screen shows correct screens`() {
        val viewModel = stubViewModel()
        coEvery { viewModel.model.getSettingsFlow() } returns MutableStateFlow(Settings("foo", Theme.BlueYellowMacaw))
        composeTestRule.setContent {
            AccountScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(ACCOUNT_SCREEN_MAIN_SCREEN_TEST_ID).assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithTag(ACCOUNT_SCREEN_MAIN_SCREEN_TEST_ID).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(ACCOUNT_SCREEN_USER_SCREECH_SCREEN_TEST_ID).assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithTag(ACCOUNT_SCREEN_USER_SCREECH_SCREEN_TEST_ID).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(ACCOUNT_SCREEN_USER_FAVORITES_SCREEN_TEST_ID).assertIsDisplayed()
    }
}