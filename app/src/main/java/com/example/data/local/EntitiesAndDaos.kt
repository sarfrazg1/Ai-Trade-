package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val isPinned: Boolean = false,
    val addedTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_signals")
data class SavedSignalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val timeframe: String,
    val signalType: String,
    val score: Int,
    val entryZone: String,
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val riskReward: Double,
    val timestamp: Long,
    val summary: String
)

@Entity(tableName = "paper_trades")
data class PaperTradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val signalType: String, // "LONG" or "SHORT"
    val entryPrice: Double,
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val margin: Double,
    val leverage: Int,
    val quantity: Double,
    val openTime: Long = System.currentTimeMillis(),
    val closeTime: Long? = null,
    val status: String = "OPEN", // "OPEN", "CLOSED_TP1", "CLOSED_TP2", "CLOSED_SL", "MANUALLY_CLOSED"
    val exitPrice: Double? = null,
    val pnlUsd: Double? = null
)

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY isPinned DESC, addedTime ASC")
    fun getAll(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<WatchlistEntity>)

    @Delete
    suspend fun delete(item: WatchlistEntity)
}

@Dao
interface SavedSignalDao {
    @Query("SELECT * FROM saved_signals ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SavedSignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(signal: SavedSignalEntity): Long

    @Delete
    suspend fun delete(signal: SavedSignalEntity)
}

@Dao
interface PaperTradeDao {
    @Query("SELECT * FROM paper_trades ORDER BY openTime DESC")
    fun getAll(): Flow<List<PaperTradeEntity>>

    @Query("SELECT * FROM paper_trades WHERE status = 'OPEN'")
    suspend fun getOpenTrades(): List<PaperTradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trade: PaperTradeEntity): Long

    @Update
    suspend fun update(trade: PaperTradeEntity)

    @Delete
    suspend fun delete(trade: PaperTradeEntity)
}

@Database(
    entities = [WatchlistEntity::class, SavedSignalEntity::class, PaperTradeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun savedSignalDao(): SavedSignalDao
    abstract fun paperTradeDao(): PaperTradeDao
}
