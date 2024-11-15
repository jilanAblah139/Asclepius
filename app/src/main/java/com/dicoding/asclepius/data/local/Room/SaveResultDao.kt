package com.dicoding.asclepius.data.local.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dicoding.asclepius.data.local.Entity.SaveResult
import kotlinx.coroutines.flow.Flow


@Dao
interface SaveResultDao {
    @Insert
    suspend fun insertResult(result: SaveResult)

    @Query("SELECT * FROM save_result")
    suspend fun getAllResult(): List<SaveResult>
}