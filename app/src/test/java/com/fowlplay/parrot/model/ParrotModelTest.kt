package com.fowlplay.parrot.model

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.fowlplay.parrot.R
import com.fowlplay.parrot.viewmodel.Settings
import com.fowlplay.parrot.viewmodel.Theme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ParrotModelTest {

    @get:Rule
    var instantTask = InstantTaskExecutorRule()

    private lateinit var database: ParrotDatabase
    private lateinit var dataStore: ParrotDataStore

    @Before
    fun before() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), ParrotDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun after() {
        database.close()
    }

    @Test
    fun `when model created loads db into room`() = testRunner {
        val lineCount = 32
        val model = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount,
            lineCount,
            StandardTestDispatcher(testScheduler)
        )

        waitForModelToLoad(model)

        Assert.assertEquals(lineCount, database.markovDao().getNodeCount())
    }

    @Test
    fun `when model created but db already loaded does not load twice`() = testRunner {
        val lineCount = 32
        val model = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount,
            lineCount,
            StandardTestDispatcher(testScheduler)
        )

        waitForModelToLoad(model)

        val model2 = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount,
            lineCount
        )

        waitForModelToLoad(model2)

        Assert.assertEquals(lineCount, database.markovDao().getNodeCount())
    }

    @Test
    fun `when model created with page size less than line count loads complete db into room`() = testRunner {
        val lineCount = 32
        val model = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount / 2,
            lineCount,
            StandardTestDispatcher(testScheduler)
        )

        waitForModelToLoad(model)

        Assert.assertEquals(lineCount, database.markovDao().getNodeCount())
    }

    @Test
    fun `when model created with page size greater than line count loads complete db into room`() = testRunner {
        val lineCount = 32
        val model = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount * 2,
            lineCount,
            StandardTestDispatcher(testScheduler)
        )

        waitForModelToLoad(model)

        Assert.assertEquals(lineCount, database.markovDao().getNodeCount())
    }

    @Test
    fun `when model created with db larger than cache loads complete db into room`() = testRunner {
        val lineCount = 75
        val model = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount,
            lineCount,
            StandardTestDispatcher(testScheduler)
        )

        waitForModelToLoad(model)

        Assert.assertEquals(lineCount, database.markovDao().getNodeCount())
    }

    @Test
    fun `when model created and loads db sets loaded flag to true`() = testRunner {
        val lineCount = 32
        val model = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount * 2,
            lineCount,
            StandardTestDispatcher(testScheduler)
        )

        waitForModelToLoad(model)

        Assert.assertTrue(model.loaded.value!!)
    }

    @Test
    fun `when model created and loads db populates screech cache`() = testRunner {
        val lineCount = 32
        val model = ParrotModel(
            ApplicationProvider.getApplicationContext(),
            database,
            dataStore,
            R.raw.db_2_2k,
            lineCount * 2,
            lineCount,
            StandardTestDispatcher(testScheduler)
        )

        waitForModelToLoad(model)

        Assert.assertTrue(model.initialCache.isNotEmpty())
    }

    @Test
    fun `when model created and no settings set returns empty settings`() = testRunner {
        val settings = getModel().getSettingsFlow().first()

        Assert.assertTrue(settings.username.isEmpty())
    }

    @Test
    fun `when model created and settings are updated uses updated settings`() = testRunner {
        val username = "foo"
        val theme = Theme.BlueYellowMacaw
        val model = getModel()
        waitForModelToLoad(model)
        model.updateSettings(Settings(username, theme))
        val settings = getModel().getSettingsFlow().first()

        Assert.assertEquals(username, settings.username)
        Assert.assertEquals(theme, settings.theme)
    }

    @Test
    fun `when adding new screech adds it to room and initialCache`() = testRunner {
        val model = getModel()
        model.initialCache.clear()
        val initialScreechCount = database.screechDao().getScreechCount()
        model.insertScreech(Screech("", ""), true)

        Assert.assertEquals(1, model.initialCache.size)
        Assert.assertTrue(database.screechDao().getScreechCount() == initialScreechCount + 1)
    }

    @Test
    fun `when updating screech does not add it to initialCache`() = testRunner {
        val model = getModel()
        model.initialCache.clear()
        val initialScreechCount = database.screechDao().getScreechCount()
        model.insertScreech(Screech("", ""), false)

        Assert.assertTrue(model.initialCache.isEmpty())
        Assert.assertTrue(database.screechDao().getScreechCount() == initialScreechCount + 1)
    }

    private fun getModel() = ParrotModel(
        ApplicationProvider.getApplicationContext(),
        database,
        dataStore,
        R.raw.db_2_2k,
        32,
        32
    )

    private suspend fun waitForModelToLoad(model: ParrotModel) {
        model.loaded.first { it }
    }

    private fun testRunner(testContents: suspend TestScope.() -> Unit) = runTest {
        dataStore = ParrotDataStore(ApplicationProvider.getApplicationContext())
        testContents.invoke(this)
        dataStore.reset()
    }
}