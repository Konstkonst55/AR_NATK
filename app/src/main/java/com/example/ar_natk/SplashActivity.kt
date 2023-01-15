package com.example.ar_natk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ar_natk.data.storage.FirstRunStorage
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.presentation.core.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkRun()
    }

    private fun checkRun() {
        when (FirstRunStorage(this).isFirstRun) {
            true -> {
                startActivity(Intent(this, OnboardActivity::class.java))
                finish()
                FirstRunStorage(this).isFirstRun = false
            }
            false -> {
                if (UserDataStorage(this).userName.isNullOrEmpty()) {
                    startActivity(Intent(this, InputNameActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}