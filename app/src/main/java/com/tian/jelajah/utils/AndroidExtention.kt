package com.tian.jelajah.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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