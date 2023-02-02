package com.fowlplay.parrot.ui.compose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fowlplay.parrot.model.Screech
import com.fowlplay.parrot.stubViewModel
import com.fowlplay.parrot.viewmodel.AppIntent
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// required to access final members on androidx.loader.content.ModernAsyncTask
// see: https://github.com/robolectric/robolectric/issues/6593
@Config(instrumentedPackages = ["androidx.loader.content"])
@RunWith(RobolectricTestRunner::class)
class ScreechCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when favorite button pressed publishes favorite intent`() {
        val viewModel = stubViewModel()
        val screech = Screech("foo", "bar")
        composeTestRule.setContent {
            ScreechCard(screech, viewModel)
        }

        composeTestRule.onNodeWithTag(SCREECH_CARD_BODY_TEST_ID).performClick()
        composeTestRule.onNodeWithTag(SCREECH_CARD_FAVORITE_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.ScreechButton.Favorite(screech.copy(favorite = true))) }
    }

    @Test
    fun `when favorite button pressed but already favorite publishes unfavorite intent`() {
        val viewModel = stubViewModel()
        val screech = Screech("foo", "bar", true)
        composeTestRule.setContent {
            ScreechCard(screech, viewModel)
        }

        composeTestRule.onNodeWithTag(SCREECH_CARD_BODY_TEST_ID).performClick()
        composeTestRule.onNodeWithTag(SCREECH_CARD_FAVORITE_BUTTON_TEST_ID).performClick()

        verify { viewModel.postIntent(AppIntent.ScreechButton.Favorite(screech.copy(favorite = false))) }
    }
}