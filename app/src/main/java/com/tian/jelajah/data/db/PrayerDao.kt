package com.tian.jelajah.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.model.Surah


@Dao
interface PrayerDao {

    @Query("SELECT * FROM prayer")
    fun getAll(): LiveData<List<Prayer>>

    @Query("SELECT * FROM prayer WHERE date=:date")
    fun getLiveData(date: String): LiveData<List<Prayer>>

    @Query("SELECT * FROM prayer WHERE date=:date")
    suspend fun get(date: String): List<Prayer>

    @Query("SELECT * FROM prayer WHERE id=:id")
    suspend fun get(id: Int): Prayer?

    @Query("SELECT * FROM prayer WHERE time=:time")
    suspend fun get(time: Long): Prayer?

    @Query("SELECT * FROM prayer WHERE time > :time ORDER BY time LIMIT 1")
    suspend fun getNext(time: Long): Prayer?

    @Query("SELECT * FROM prayer WHERE date=:date")
    fun getNext(date: String): List<Prayer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Prayer>)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertSurah(list: List<Surah>)
//
//    @Query("SELECT * FROM surah WHERE number=:number")
//    suspend fun getSurahid(number: String): Surah?

    @Query("DELETE FROM prayer")
    suspend fun deleteAll()

//    @Query("DELETE FROM surah")
//    suspend fun deleteSurah()

}