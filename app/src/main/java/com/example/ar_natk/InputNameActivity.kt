package com.example.ar_natk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.databinding.ActivityInputNameBinding
import com.example.ar_natk.presentation.core.MainActivity
import com.example.ar_natk.utils.DeviceName
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InputNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputNameBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        hideProgress()

        binding.fabReady.setOnClickListener {
            if (binding.etName.text.isNullOrEmpty()) {
                binding.tilUserName.error = getString(R.string.t_empty_input_error)
            } else {
                binding.tilUserName.error = ""
                tryCreateAccount()
            }
        }
    }

    private fun tryCreateAccount() {
        showProgress()

        val name = binding.etName.text.toString()

        val user = hashMapOf(
            "name" to name,
            "count" to 0,
            "device" to DeviceName.deviceName(),
            "date" to Timestamp.now()
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                saveUserData(name, it.id)
                startActivity(Intent(this, MainActivity::class.java))
                hideProgress()
                finish()
            }
            .addOnFailureListener {
                hideProgress()
                Log.e("firebase", it.message.toString())
                Snackbar.make(
                    binding.root,
                    getString(R.string.t_connection_error),
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }

    private fun showProgress() {
        binding.iLoader.root.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.iLoader.root.visibility = View.GONE
    }

    private fun saveUserData(name: String, id: String) {
        with(UserDataStorage(this)) {
            userName = name
            userId = id
        }
    }
}