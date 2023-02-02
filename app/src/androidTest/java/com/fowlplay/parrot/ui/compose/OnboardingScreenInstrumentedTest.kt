package com.fowlplay.parrot.ui.compose

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.fowlplay.parrot.R
import com.fowlplay.parrot.model.ParrotDataStore
import com.fowlplay.parrot.model.ParrotDatabase
import com.fowlplay.parrot.model.ParrotModel
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.viewmodel.ViewState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class OnboardingScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenSubmitButtonClicked_withValidUsername_submits() = runTest {
        val context: Context = ApplicationProvider.getApplicationContext()
        val viewModel = ParrotViewModel(
            ParrotModel(
                context,
                ParrotDatabase.getDatabase(context),
                ParrotDataStore(context).also { it.reset() },
                R.raw.db_2_2k
            )
        )
        composeTestRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_USERNAME_TEXTFIELD_TEST_ID).performTextInput("foo")
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_SUBMIT_BUTTON_TEST_ID).performClick()

        val viewState = viewModel.state.first { it.view == ViewState.Cacophony }

        Assert.assertEquals(ViewState.Cacophony, viewState.view)
    }
}