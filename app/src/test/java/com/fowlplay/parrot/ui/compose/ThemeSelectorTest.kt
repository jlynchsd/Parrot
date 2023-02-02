package com.fowlplay.parrot.ui.compose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.fowlplay.parrot.stubViewModel
import com.fowlplay.parrot.viewmodel.AppIntent
import com.fowlplay.parrot.viewmodel.Theme
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
class ThemeSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when blue yellow macaw selected updates theme`() {
        val viewModel = stubViewModel()
        val theme = Theme.BlueYellowMacaw
        composeTestRule.setContent {
            ThemeSelector(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(getThemeSelectorButtonTestId(theme)).performClick()

        verify { viewModel.postIntent(AppIntent.UpdateTheme(theme)) }
    }

    @Test
    fun `when scarlet macaw selected updates theme`() {
        val viewModel = stubViewModel()
        val theme = Theme.ScarletMacaw
        composeTestRule.setContent {
            ThemeSelector(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(getThemeSelectorButtonTestId(theme)).performClick()

        verify { viewModel.postIntent(AppIntent.UpdateTheme(theme)) }
    }

    @Test
    fun `when amazon selected updates theme`() {
        val viewModel = stubViewModel()
        val theme = Theme.Amazon
        composeTestRule.setContent {
            ThemeSelector(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(getThemeSelectorButtonTestId(theme)).performClick()

        verify { viewModel.postIntent(AppIntent.UpdateTheme(theme)) }
    }

    @Test
    fun `when african grey selected updates theme`() {
        val viewModel = stubViewModel()
        val theme = Theme.AfricanGrey
        composeTestRule.setContent {
            ThemeSelector(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(getThemeSelectorButtonTestId(theme)).performClick()

        verify { viewModel.postIntent(AppIntent.UpdateTheme(theme)) }
    }
}