package com.fowlplay.parrot.model

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.fowlplay.parrot.R
import com.fowlplay.parrot.viewmodel.Settings
import com.fowlplay.parrot.viewmodel.Theme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class DatabasePagingSourceTest {

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
    fun `when paging user screeches but model is empty returns empty set`() = testRunner {
        val databasePagingSource = DatabasePagingSource(getModel(), false)
        val result = databasePagingSource.load(PagingSource.LoadParams.Refresh(0, 5, false))

        Assert.assertTrue(result.data.isEmpty())
    }

    @Test
    fun `when paging favorite screeches but model is empty returns empty set`() = testRunner {
        val databasePagingSource = DatabasePagingSource(getModel(), true)
        val result = databasePagingSource.load(PagingSource.LoadParams.Refresh(0, 5, false))

        Assert.assertTrue(result.data.isEmpty())
    }

    @Test
    fun `when paging user screeches returns data`() = testRunner {
        val username = "foo"
        dataStore.updateSettings(Settings(username, Theme.BlueYellowMacaw))
        database.screechDao().insert(Screech(username, "bar", false))
        val databasePagingSource = DatabasePagingSource(getModel(), false)
        val result = databasePagingSource.load(PagingSource.LoadParams.Refresh(0, 5, false))

        Assert.assertEquals(1, result.data.size)
    }

    @Test
    fun `when paging favorite screeches returns data`() = testRunner {
        val username = "foo"
        database.screechDao().insert(Screech(username, "bar", true))
        val databasePagingSource = DatabasePagingSource(getModel(), true)
        val result = databasePagingSource.load(PagingSource.LoadParams.Refresh(0, 5, false))

        Assert.assertEquals(1, result.data.size)
    }

    private fun getModel() = ParrotModel(
        ApplicationProvider.getApplicationContext(),
        database,
        dataStore,
        R.raw.db_2_2k,
        32,
        32
    )

    private fun testRunner(testContents: suspend TestScope.() -> Unit) = runTest {
        dataStore = ParrotDataStore(ApplicationProvider.getApplicationContext())
        testContents.invoke(this)
        dataStore.reset()
    }
}