package com.example.ar_natk.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ar_natk.R
import com.example.ar_natk.data.storage.FirstRunStorage
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.presentation.auth.AuthActivity
import com.example.ar_natk.presentation.core.MainActivity
import com.example.ar_natk.presentation.onboard.OnboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}