package com.example.conecta4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.conecta4.dataDao.Jugador
import com.example.conecta4.dataDao.InterjugadorDao

@Database(entities = [Jugador::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun InterjugadorDao(): InterjugadorDao

    companion object {
        @Volatile
        private var INSTANCE : AppDatabase? = null
        fun getDatabase (context: Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Conecta4_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}