package com.example.ar_natk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun String.toBitmap(context: Context): Bitmap? {
    return BitmapFactory.decodeResource(context.resources, this.toResourceId(context))
}

@SuppressLint("DiscouragedApi")
private fun String.toResourceId(context: Context): Int {
    return context.resources.getIdentifier(
        this,
        Constants.DEF_TYPE_DRAWABLE,
        context.packageName
    )
}
