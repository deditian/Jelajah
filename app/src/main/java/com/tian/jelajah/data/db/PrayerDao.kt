package com.tian.jelajah.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tian.jelajah.model.Prayer


@Dao
interface PrayerDao {

    @Query("SELECT * FROM prayer")
    fun getAll(): LiveData<List<Prayer>>

    @Query("SELECT * FROM prayer WHERE date=:date")
    fun getLiveData(date: String): LiveData<List<Prayer>>

    @Query("SELECT * FROM prayer WHERE date=:date")
    fun get(date: String): List<Prayer>

    @Query("SELECT * FROM prayer WHERE id=:id")
    fun get(id: Int): Prayer?

    @Query("SELECT * FROM prayer WHERE time=:time")
    fun get(time: Long): Prayer?

    @Query("SELECT * FROM prayer WHERE time > :time ORDER BY time LIMIT 1")
    fun getNext(time: Long): Prayer?

    @Query("SELECT * FROM prayer WHERE date=:date")
    fun getNext(date: String): List<Prayer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Prayer>)

    @Query("DELETE FROM prayer")
    fun deleteAll()

}