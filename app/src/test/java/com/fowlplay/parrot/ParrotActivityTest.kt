package com.fowlplay.parrot

import androidx.test.core.app.ActivityScenario
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.viewmodel.State
import com.fowlplay.parrot.viewmodel.Theme
import com.fowlplay.parrot.viewmodel.ViewState
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// required to access final members on androidx.loader.content.ModernAsyncTask
// see: https://github.com/robolectric/robolectric/issues/6593
@Config(instrumentedPackages = ["androidx.loader.content"])
@RunWith(RobolectricTestRunner::class)
class ParrotActivityTest {

    private lateinit var state: MutableStateFlow<State>

    @Before
    fun before() {
        state = MutableStateFlow(State(ViewState.SplashPage , Theme.ScarletMacaw))
        mockkConstructor(ParrotViewModel::class)
        coEvery { anyConstructed<ParrotViewModel>().state } returns state
    }

    @After
    fun after() {
        clearAllMocks()
    }

    @Test
    fun `when back button pressed with no valid previous pages finishes activity`() {
        ActivityScenario.launch(ParrotActivity::class.java).use {
            it.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()

                Assert.assertTrue(activity.isFinishing)
            }
        }
    }

    // TODO figure out why constructor mock can't override state
//    @Test
//    fun `when back button pressed after navigating from temporary page finishes activity`() {
//        ActivityScenario.launch(ParrotActivity::class.java).use { scenario ->
//            scenario.onActivity { activity ->
//                navigateState(ViewState.Cacophony)
//                activity.onBackPressedDispatcher.onBackPressed()
//
//                Assert.assertTrue(activity.isFinishing)
//            }
//        }
//    }


//    @Test
//    fun `when back button pressed after navigating from valid page does not finish activity`() {
//        ActivityScenario.launch(ParrotActivity::class.java).use { scenario ->
//            scenario.onActivity { activity ->
//                navigateState(ViewState.Cacophony)
//                navigateState(ViewState.AboutPage)
//                activity.onBackPressedDispatcher.onBackPressed()
//
//                Assert.assertFalse(activity.isFinishing)
//            }
//        }
//    }

    private fun navigateState(destination: ViewState) {
        state.value = state.value!!.copy(view = destination)
    }
}