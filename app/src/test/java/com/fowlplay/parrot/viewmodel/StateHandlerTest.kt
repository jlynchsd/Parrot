package com.fowlplay.parrot.viewmodel

import androidx.paging.PagingSource
import com.fowlplay.parrot.model.ParrotModel
import com.fowlplay.parrot.model.Screech
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class StateHandlerTest {

    private lateinit var state: MutableStateFlow<State>
    private lateinit var emptyPagingSources: Array<PagingSource<Int, Screech>?>

    private val settings = Settings("foo", Theme.BlueYellowMacaw)

    @Before
    fun before() {
        state = MutableStateFlow(State(ViewState.SplashPage , Theme.BlueYellowMacaw))
        emptyPagingSources = emptyArray()
    }


    @Test
    fun `when created with no settings and database loaded state is onboarding page`() = runTest {
        val model = stubModel()
        val currentSettings = Settings("", Theme.BlueYellowMacaw)
        every { model.loaded } returns MutableStateFlow(true)
        coEvery { model.getSettingsFlow() } returns MutableStateFlow(currentSettings)

        val stateHandler = StateHandler(state, currentSettings, model, emptyPagingSources).also {
            it.initHandler()
        }

        Assert.assertEquals(ViewState.OnboardingPage, state.value!!.view)
    }

    @Test
    fun `when created with no settings and database is not loaded waits for db to load before onboarding page`() = runTest {
        val model = stubModel()
        val loaded = MutableStateFlow(false)
        val currentSettings = Settings("", Theme.BlueYellowMacaw)
        every { model.loaded } returns loaded
        coEvery { model.getSettingsFlow() } returns MutableStateFlow(currentSettings)

        val stateHandler = StateHandler(state, currentSettings, model, emptyPagingSources).also {
            launch {
                it.initHandler()
            }
        }

        Assert.assertEquals(ViewState.SplashPage, state.value!!.view)

        loaded.value = true

        val updatedState = state.first {it.view == ViewState.OnboardingPage}

        Assert.assertEquals(ViewState.OnboardingPage, updatedState.view)
    }

    @Test
    fun `when created with settings and database loaded state is cacophony page`() = runTest {
        val model = stubModel()
        every { model.loaded } returns MutableStateFlow(true)
        coEvery { model.getSettingsFlow() } returns MutableStateFlow(Settings("foo", Theme.BlueYellowMacaw))

        val stateHandler = StateHandler(state, settings, model, emptyPagingSources).also {
            it.initHandler()
        }

        Assert.assertEquals(ViewState.Cacophony, state.value!!.view)
    }

    @Test
    fun `when created with settings and database is not loaded waits for db to load before cacophony page`() = runTest {
        val model = stubModel()
        val loaded = MutableStateFlow(false)
        every { model.loaded } returns loaded
        coEvery { model.getSettingsFlow() } returns MutableStateFlow(Settings("foo", Theme.BlueYellowMacaw))

        val stateHandler = StateHandler(state, settings, model, emptyPagingSources).also {
            launch {
                it.initHandler()
            }
        }

        Assert.assertEquals(ViewState.SplashPage, state.value!!.view)

        loaded.value = true

        val updatedState = state.first {it.view == ViewState.Cacophony}

        Assert.assertEquals(ViewState.Cacophony, updatedState.view)
    }

    @Test
    fun `when intent is update theme updates the theme`() = runTest {
        val stateHandler = StateHandler(state, settings, stubModel(), emptyPagingSources)

        Assert.assertNotEquals(Theme.ScarletMacaw, state.value!!.theme)
        stateHandler.handleIntent(AppIntent.UpdateTheme(Theme.ScarletMacaw))
        Assert.assertEquals(Theme.ScarletMacaw, state.value!!.theme)
    }

    @Test
    fun `when intent is navigate account screen updates the view state`() = runTest {
        val stateHandler = StateHandler(state, settings, stubModel(), emptyPagingSources)

        Assert.assertNotEquals(ViewState.AccountPage, state.value!!.view)
        stateHandler.handleIntent(AppIntent.BottomBar.AccountHome)
        Assert.assertEquals(ViewState.AccountPage, state.value!!.view)
    }

    @Test
    fun `when intent is navigate cacophony screen updates the view state`() = runTest {
        val stateHandler = StateHandler(state, settings, stubModel(), emptyPagingSources)
        state.value = state.value!!.copy(view = ViewState.AccountPage)

        Assert.assertNotEquals(ViewState.Cacophony, state.value!!.view)
        stateHandler.handleIntent(AppIntent.BottomBar.Cacophony)
        Assert.assertEquals(ViewState.Cacophony, state.value!!.view)
    }

    @Test
    fun `when intent is navigate about screen updates the view state`() = runTest {
        val stateHandler = StateHandler(state, settings, stubModel(), emptyPagingSources)

        Assert.assertNotEquals(ViewState.AboutPage, state.value!!.view)
        stateHandler.handleIntent(AppIntent.BottomBar.About)
        Assert.assertEquals(ViewState.AboutPage, state.value!!.view)
    }

    @Test
    fun `when intent is display screech input updates the input state`() = runTest {
        val stateHandler = StateHandler(state, settings, stubModel(), emptyPagingSources)
        state.value = state.value!!.copy(displayScreechInput = false)

        Assert.assertFalse(state.value!!.displayScreechInput)
        stateHandler.handleIntent(AppIntent.BottomBar.NewScreech)
        Assert.assertTrue(state.value!!.displayScreechInput)
    }

    @Test
    fun `when intent is create new user adds user to settings and sets state to cacophony screen`() = runTest {
        val model = stubModel()
        coEvery { model.getSettingsFlow() } returns MutableStateFlow(Settings("", Theme.BlueYellowMacaw))
        val stateHandler = StateHandler(state, settings, model, emptyPagingSources)

        Assert.assertNotEquals(ViewState.Cacophony, state.value!!.view)

        stateHandler.handleIntent(AppIntent.OnboardingScreen.CreateUser("foo"))

        coVerify { model.updateSettings(any()) }
        Assert.assertEquals(ViewState.Cacophony, state.value!!.view)
    }

    @Test
    fun `when intent is create new screech adds screech to database and hides screech input`() = runTest {
        val model = stubModel()
        state.value = state.value!!.copy(displayScreechInput = true)
        val stateHandler = StateHandler(state, settings, model, emptyPagingSources)

        stateHandler.handleIntent(AppIntent.ScreechInput.Submit(""))

        coVerify { model.insertScreech(any(), true) }
        Assert.assertFalse(state.value!!.displayScreechInput)
    }

    @Test
    fun `when intent is cancel new screech does not add screech to database and hides screech input`() = runTest {
        val model = stubModel()
        state.value = state.value!!.copy(displayScreechInput = true)
        val stateHandler = StateHandler(state, settings, model, emptyPagingSources)

        stateHandler.handleIntent(AppIntent.ScreechInput.Cancel)

        coVerify(exactly = 0) { model.insertScreech(any(), true) }
        Assert.assertFalse(state.value!!.displayScreechInput)
    }

    @Test
    fun `when intent is favorite screech adds screech to database and invalidates persistent pagers`() = runTest {
        val model = stubModel()
        val pagingSources: Array<PagingSource<Int, Screech>?> = arrayOf(
            mockk(relaxed = true),
            null
        )
        val stateHandler = StateHandler(state, settings, model, pagingSources)

        stateHandler.handleIntent(AppIntent.ScreechButton.Favorite(Screech("", "")))

        coVerify { model.insertScreech(any(), false) }
        verify { pagingSources.first()!!.invalidate() }
    }

    private fun stubModel() =  mockk<ParrotModel>(relaxed = true).also {
        every { it.loaded } returns MutableStateFlow(true)
        coEvery { it.getSettingsFlow() } returns MutableStateFlow(Settings("foo", Theme.BlueYellowMacaw))
    }

}