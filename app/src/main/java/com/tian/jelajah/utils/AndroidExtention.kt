package com.tian.jelajah.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.SpriteFactory
import com.github.ybq.android.spinkit.Style
import com.google.common.util.concurrent.ListenableFuture
import com.tian.jelajah.R
import com.tian.jelajah.model.DataJadwal
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.model.Surah
import com.tian.jelajah.model.SurahList
import java.lang.reflect.Modifier
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.reflect.KClass

fun <K: Activity> Activity.gotoActivity(klass: KClass<K>) {
    startActivity(Intent(this, klass.java))
}

fun <K: Activity, T : Parcelable> Activity.gotoActivityWithData(klass: KClass<K>, key: String, data: T) {
    val intent = Intent(this, klass.java)
    intent.putExtra(key, data)
    startActivity(intent)

}


fun <K: Activity> Activity.gotoActivityNewTask(klass: KClass<K>) {
    startActivity(Intent(this, klass.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    finish()
}

fun <K: Activity> Fragment.gotoActivity(klass: KClass<K>) {
    startActivity(Intent(requireActivity(), klass.java))
}

@SuppressLint("SimpleDateFormat")
fun dateFormat(format: String? = null, time: Long? = null) : String {
    val current = SimpleDateFormat(format ?: "yyyy-MM-dd")
    return current.format(if (time != null) Date(time) else Date())
}

@SuppressLint("SimpleDateFormat")
fun dateFormatParse(format: String?, time: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    val parse = parser.parse(time)
    return formatter.format(parse!!)
}


@SuppressLint("SimpleDateFormat")
fun createTimeString(elapsedTime: Long): String {
    val time: String
    val df = SimpleDateFormat("HH:mm:ss")
    df.timeZone = TimeZone.getTimeZone("GMT")
    time = df.format(elapsedTime)
    return time
}

fun DataJadwal.convertToList() : List<Prayer> {
    val list = ArrayList<Prayer>()
    list.add(Prayer(
        name = "imsak",
        time = ("$tanggal ${jadwal.Imsak}").dateTimeStringToLong(),
        backgroundColor = Color.parseColor("#0f0c29"),
        date = tanggal, type = "notify"))
    list.add(Prayer(
        name = "fajr",
        time = ("$tanggal ${jadwal.Subuh}").dateTimeStringToLong(),
        backgroundColor = Color.parseColor("#0f0c29"),
        date = tanggal, type = "adzan"))
    list.add(Prayer(
        name = "sunrise",
        time = ("$tanggal ${jadwal.Terbit}").dateTimeStringToLong(),
        backgroundColor = Color.parseColor("#0f0c29"),
        date = tanggal, type = "notify"))
    list.add(Prayer(
        name = "dhuhr",
        time = ("$tanggal ${jadwal.Duhur}").dateTimeStringToLong(),
        backgroundColor = Color.parseColor("#0a9de1"),
        date = tanggal, type = "adzan"))
    list.add(Prayer(
        name = "asr",
        time = ("$tanggal ${jadwal.Ashar}").dateTimeStringToLong(),
        backgroundColor = Color.parseColor("#0a9de1"),
        date = tanggal, type = "adzan"))
    list.add(Prayer(
        name = "maghrib",
        time = ("$tanggal ${jadwal.Maghrib}").dateTimeStringToLong(),
        backgroundColor = Color.parseColor("#ff5231"),
        date = tanggal, type = "adzan"))
    list.add(Prayer(
        name = "isha",
        time = ("$tanggal ${jadwal.Isya}").dateTimeStringToLong(),
        backgroundColor = Color.parseColor("#6f4ba9"),
        date = tanggal, type = "adzan"))
    return list
}

//fun Surah.convertToList() : List<SurahList> {
//    val list = ArrayList<SurahList>()
//    list.add(SurahList(
//        name = "imsak",
//        time = ("$tanggal ${jadwal.Imsak}").dateTimeStringToLong(),
//        backgroundColor = Color.parseColor("#0f0c29"),
//        date = tanggal, type = "notify"))
//    list.add(SurahList(
//        name = "fajr",
//        time = ("$tanggal ${jadwal.Subuh}").dateTimeStringToLong(),
//        backgroundColor = Color.parseColor("#0f0c29"),
//        date = tanggal, type = "adzan"))
//    list.add(SurahList(
//        name = "sunrise",
//        time = ("$tanggal ${jadwal.Terbit}").dateTimeStringToLong(),
//        backgroundColor = Color.parseColor("#0f0c29"),
//        date = tanggal, type = "notify"))
//    list.add(SurahList(
//        name = "dhuhr",
//        time = ("$tanggal ${jadwal.Duhur}").dateTimeStringToLong(),
//        backgroundColor = Color.parseColor("#0a9de1"),
//        date = tanggal, type = "adzan"))
//    list.add(SurahList(
//        name = "asr",
//        time = ("$tanggal ${jadwal.Ashar}").dateTimeStringToLong(),
//        backgroundColor = Color.parseColor("#0a9de1"),
//        date = tanggal, type = "adzan"))
//    list.add(SurahList(
//        name = "maghrib",
//        time = ("$tanggal ${jadwal.Maghrib}").dateTimeStringToLong(),
//        backgroundColor = Color.parseColor("#ff5231"),
//        date = tanggal, type = "adzan"))
//    list.add(SurahList(
//        name = "isha",
//        time = ("$tanggal ${jadwal.Isya}").dateTimeStringToLong(),
//        backgroundColor = Color.parseColor("#6f4ba9"),
//        date = tanggal, type = "adzan"))
//    return list
//}

fun String.dateTimeStringToLong() : Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return try {
        val today = dateFormat.parse(this)
        today?.time?:0
    } catch (e: ParseException) {
        0
    }
}

fun Long.isValid(time: Long? = null) : Boolean {
    return this >= time?:Date().time
}

fun Activity.getStringWithNameId(nameId: String) : String {
    return getString(nameId.nameResource(R.string::class.java))
}

@Throws(IllegalAccessException::class)
fun String.nameResource(clazz: Class<*>) : Int  {
    var value = 0
    val c = clazz.declaringClass
    for (f in clazz.declaredFields) {
        if (Modifier.isStatic(f.modifiers)) {
            val wasAccessible = f.isAccessible
            f.isAccessible = true
            if (f.name == this) {
                value = f.getInt(c)
                break
            }
            f.isAccessible = wasAccessible
        }
    }
    return value
}

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    if (manager != null) {
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                logi("servis run")
                return true
            }
        }
    }
    logi("servis gak trun")
    return false
}

fun logi(msg: String) = Log.i("JELAJAH_LOG", msg)
fun loge(msg: String) = Log.e("JELAJAH_LOG ERROR", msg)

fun isWorkScheduled(context: Context, tag: String): Boolean {
    val instance = WorkManager.getInstance(context)
    val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosByTag(tag)
    return try {
        var running = false
        val workInfoList: List<WorkInfo> = statuses.get()
        for (workInfo in workInfoList) {
            val state = workInfo.state
            running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
        }
        logi("service is run = $running")
        running
    } catch (e: ExecutionException) {
        e.printStackTrace()
        false
    } catch (e: InterruptedException) {
        e.printStackTrace()
        false
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Activity.isNetworkAvailable(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
    } else {
        try {
            val activeNetworkInfo = manager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}