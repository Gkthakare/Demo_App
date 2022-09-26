package com.example.registrationscreen.utils

import android.app.Activity
import android.app.AlertDialog
import com.example.registrationscreen.R

class LoadingActivity(private val mActivity: Activity) {
    private lateinit var dialog: AlertDialog
    fun startLoading() {
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item,null)
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }
    fun isDismiss(){
        dialog.dismiss()
    }
}