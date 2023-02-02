package com.fowlplay.parrot.model

import android.content.Context
import androidx.annotation.RawRes
import com.fowlplay.parrot.viewmodel.Settings
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.zip.ZipInputStream
import kotlin.random.Random

class ParrotModel(
    context: Context,
    private val database: ParrotDatabase,
    private val dataStore: ParrotDataStore,
    @RawRes private val resId: Int,
    dbPageSize: Int = 5000,
    limit: Int? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
    private val applicationContext = context.applicationContext

    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded

    val initialCache = emptyList<Screech>().toMutableList()

    init {
        CoroutineScope(dispatcher).launch {
            _loaded.value = loadDb(resId, dbPageSize, limit)
        }
    }

    private suspend fun loadDb(resId: Int, dbPageSize: Int, limit: Int?): Boolean {
        return withContext(Dispatchers.IO) {
//            database.markovDao().deleteAll()
            if (database.markovDao().getNodeCount() == 0) {
                applicationContext.resources.openRawResource(resId).let { inputStream ->
                    val zipInputStream = ZipInputStream(inputStream)
                    zipInputStream.nextEntry?.let { zipEntry ->
                        val buffer = ByteArray(32 * 1024)
                        val lineLimit = limit ?: Int.MAX_VALUE
                        var readCount = zipInputStream.read(buffer)
                        var lineBuffer = ""

                        while (readCount != -1) {
                            lineBuffer += String(buffer, 0, readCount)

                            val evenSplit = lineBuffer.endsWith(LINEBREAK)
                            val lines = lineBuffer.split(LINEBREAK)

                            if (lines.size > dbPageSize || lines.size >= lineLimit) {
                                lineBuffer = if (evenSplit) {
                                    generateMarkovNodes(lines, database)
                                    ""
                                } else {
                                    generateMarkovNodes(lines.subList(0, lines.size - 1), database)
                                    lines.last()
                                }
                            }

                            readCount = if (lines.size >= lineLimit) {
                                -1
                            } else {
                                zipInputStream.read(buffer)
                            }
                        }

                        if (lineBuffer.isNotEmpty()) {
                            generateMarkovNodes(lineBuffer.split(LINEBREAK), database)
                        }
                    }
                    zipInputStream.closeEntry()
                    inputStream.close()
                }
            }

            for (i in 0 until 45) {
                initialCache.add(generateScreech())
            }
            return@withContext true
        }
    }

    suspend fun generateScreech(): Screech {
        var phrase = generateMarkovPhrase(database)
        var attempts = 1
        while (phrase.split(" ").size < 10 && attempts++ < 3) {
            phrase = generateMarkovPhrase(database)
        }
        val username = generateUsername()

        if (Random.nextFloat() > .7f) {
            phrase += "  " + generateEmojis(2, 6)
        }

        return Screech(username, phrase.replace("@User", generateUsername()))
    }

    fun getSettingsFlow() = dataStore.settingsFlow

    suspend fun updateSettings(settings: Settings) = dataStore.updateSettings(settings)

    suspend fun insertScreech(screech: Screech, newScreech: Boolean = false) =
        withContext(Dispatchers.IO) {
            database.screechDao().insert(screech).also {
                if (newScreech) {
                    initialCache.add(screech.copy(uid = it.toInt()))
                }
            }
        }

    suspend fun pageUserScreeches(username: String, offset: Int, count: Int) =
        withContext(Dispatchers.IO) {
            database.screechDao().pageUserScreeches(username, offset, count)
        }

    suspend fun pageFavoriteScreeches(offset: Int, count: Int) =
        withContext(Dispatchers.IO) {
            database.screechDao().pageFavoriteScreeches(offset, count)
        }

    private companion object {
        const val LINEBREAK = "\r\n"
    }
}
