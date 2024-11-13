package com.dicoding.asclepius.data.local.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.Entity.SaveResult

@Database(entities = [SaveResult::class], version = 1)
abstract class ResultDatabase : RoomDatabase() {
    abstract fun saveResultDao(): SaveResultDao

    companion object {
        @Volatile
        private var INSTANCE: ResultDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): ResultDatabase{
            if (INSTANCE == null){
                synchronized(ResultDatabase::class.java){
                    INSTANCE =  Room.databaseBuilder(
                        context.applicationContext,
                        ResultDatabase::class.java, "result_database.db"
                    )
                        .build()
                }
            }
            return INSTANCE as ResultDatabase
        }

    }
}