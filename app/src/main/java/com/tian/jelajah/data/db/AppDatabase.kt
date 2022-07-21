package com.tian.jelajah.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tian.jelajah.model.Prayer


@Database(entities = [
    Prayer::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun prayerDao() : PrayerDao

    companion object {
        const val DATABASE_NAME = "app-database.db"

        fun newInstance(context: Context) : AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}