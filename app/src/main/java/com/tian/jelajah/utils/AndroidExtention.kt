package com.tian.jelajah.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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