package com.example.android_development_practices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE `key` = :key")
    suspend fun remove(key: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE `key` = :key)")
    suspend fun contains(key: String): Boolean

    @Query("SELECT * FROM favorites WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): FavoriteEntity?

    @Query("UPDATE favorites SET detailPayload = :payload WHERE `key` = :key")
    suspend fun updateDetail(key: String, payload: String)
}
