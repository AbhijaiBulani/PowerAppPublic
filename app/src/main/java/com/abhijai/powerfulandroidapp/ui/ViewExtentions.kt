package com.abhijai.powerfulandroidapp.ui

import android.content.Context
import android.widget.Toast
import com.abhijai.powerfulandroidapp.R
import com.afollestad.materialdialogs.MaterialDialog


fun Context.displayToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Context.displaySuccessDialog(message:String){
    MaterialDialog(this)
        .show {
            title(R.string.success_dialog_title)
            message(text = message)
            positiveButton(R.string.positive_btn_text)
        }
}

fun Context.displayErrorDialog(message:String){
    MaterialDialog(this)
        .show {
            title(R.string.error_dialog_title)
            message(text = message)
            positiveButton (R.string.positive_btn_text)
        }
}