package com.tian.jelajah.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Parcelable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tian.jelajah.R
import com.tian.jelajah.model.DataJadwal
import com.tian.jelajah.model.Prayer
import java.lang.reflect.Modifier
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

fun Activity.checkAndRequestPermission(
    title: String, message: String,
    manifestPermission: String, requestCode: Int,
    action: () -> Unit
) {
    val permissionStatus = ContextCompat.checkSelfPermission(applicationContext, manifestPermission)

    if (permissionStatus == PackageManager.PERMISSION_DENIED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, manifestPermission)) {
            applicationContext.showConfirmDialog(title, message) {
                requestPermission(manifestPermission, requestCode)
            }
        } else {
            // No explanation needed -> request the permission
            requestPermission(manifestPermission, requestCode)
        }
    } else {
        action()
    }
}

fun Activity.requestPermission(manifestPermission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(manifestPermission), requestCode)
}

fun Context.showConfirmDialog(title: String, message: String, actionIfAgree: () -> Unit) {
    val alertDialog = AlertDialog.Builder(this).create()
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
        dialog.dismiss()
    }
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialog, _ ->
        actionIfAgree()
        dialog.dismiss()
    }
    alertDialog.show()
}


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