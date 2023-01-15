package com.example.ar_natk.utils

import android.os.Build
import android.text.TextUtils


class DeviceName {

    companion object {
        fun deviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL

            return if (model.startsWith(manufacturer)) {
                model.capitalize()
            } else manufacturer.capitalize() + " " + model
        }

        private fun String.capitalize(): String {
            if (TextUtils.isEmpty(this)) {
                return this
            }

            val arr = this.toCharArray()
            var capitalizeNext = true
            val phrase = StringBuilder()

            for (c in arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                    phrase.append(c.uppercaseChar())
                    capitalizeNext = false
                    continue
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true
                }
                phrase.append(c)
            }

            return phrase.toString()
        }
    }
}