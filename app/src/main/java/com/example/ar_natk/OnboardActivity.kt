package com.example.ar_natk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.databinding.ActivityOnboardBinding
import com.example.ar_natk.presentation.core.MainActivity

class OnboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabNext.setOnClickListener {
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