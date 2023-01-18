package com.example.ar_natk.presentation.onboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.databinding.ActivityOnboardBinding
import com.example.ar_natk.presentation.auth.AuthActivity
import com.example.ar_natk.presentation.core.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabNext.setOnClickListener {
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