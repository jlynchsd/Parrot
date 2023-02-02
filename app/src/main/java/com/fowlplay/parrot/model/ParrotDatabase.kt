package com.fowlplay.parrot.model

import android.content.Context
import androidx.room.*
import org.json.JSONArray

@Database(entities = [MarkovNode::class, Screech::class], version = 1)
@TypeConverters(Converters::class)
abstract class ParrotDatabase: RoomDatabase() {
    abstract fun markovDao(): MarkovDao
    abstract fun screechDao(): ScreechDao

    companion object {
        @Volatile
        private var INSTANCE: ParrotDatabase? = null

        fun getDatabase(context: Context): ParrotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParrotDatabase::class.java,
                    "parrot_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Entity
data class MarkovNode(
    @ColumnInfo(name = "prefix") val prefix: String,
    @ColumnInfo(name = "root") val root: Boolean,
    @ColumnInfo(name = "suffixes") val suffixes: MutableList<String>,
    @PrimaryKey(autoGenerate = true) val uid: Int = 0
)

@Dao
interface MarkovDao {
    @Query("SELECT * FROM MarkovNode WHERE prefix LIKE :prefix LIMIT 1")
    suspend fun findByPrefix(prefix: String): MarkovNode?

    @Query("SELECT * FROM MarkovNode WHERE root = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomRootNode(): MarkovNode?

    @Query("SELECT COUNT(uid) FROM MarkovNode")
    suspend fun getNodeCount(): Int?

    @Insert
    suspend fun insertAll(vararg markovNodes: MarkovNode)

    @Query("DELETE FROM MarkovNode")
    suspend fun deleteAll()
}

@Entity
data class Screech(
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "favorite") val favorite: Boolean = false,
    @PrimaryKey(autoGenerate = true) val uid: Int = 0
)

@Dao
interface ScreechDao {
    @Query("SELECT COUNT(uid) FROM Screech")
    suspend fun getScreechCount(): Int

    @Query("SELECT * FROM Screech WHERE username LIKE :name ORDER BY uid LIMIT :count OFFSET :offset")
    suspend fun pageUserScreeches(name: String, offset: Int, count: Int): List<Screech>

    @Query("SELECT * FROM Screech WHERE favorite = 1 ORDER BY uid LIMIT :count OFFSET :offset")
    suspend fun pageFavoriteScreeches(offset: Int, count: Int): List<Screech>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(screech: Screech): Long

    @Query("DELETE FROM Screech")
    suspend fun deleteAll()
}

class Converters {
    @TypeConverter
    fun fromString(suffixList: String) =
        emptyList<String>().toMutableList().also { mutableList ->
            JSONArray(suffixList).let {
                for (i in 0 until it.length()) {
                    mutableList.add(it.getString(i))
                }
            }

        }

    @TypeConverter
    fun toString(suffixList: MutableList<String>) =
        JSONArray(suffixList).toString()
}