package com.fowlplay.parrot.ui.compose

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// required to access final members on androidx.loader.content.ModernAsyncTask
// see: https://github.com/robolectric/robolectric/issues/6593
@Config(instrumentedPackages = ["androidx.loader.content"])
@RunWith(RobolectricTestRunner::class)
class TopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when title passed in displays title`() {
        val title = "foo"
        composeTestRule.setContent {
            TopBar(title)
        }

        composeTestRule.onNodeWithTag(TOP_BAR_TITLE_TEST_ID).assertTextEquals(title)
    }
}