package com.dicoding.asclepius.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "save_result")
data class SaveResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String = "",
    val result: String = "",
    val confidenceScore: String
)
