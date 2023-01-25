package com.example.ar_natk.presentation.core

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ar_natk.R
import com.example.ar_natk.data.storage.FirstRunStorage
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.databinding.ActivityMainBinding
import com.example.ar_natk.presentation.auth.AuthActivity
import com.example.ar_natk.presentation.collection.CollectionFragmentDirections
import com.example.ar_natk.presentation.onboard.OnboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AR_NATK)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMain)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)

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
                    navView = binding.navView

                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
                    navController = navHostFragment.navController

                    setupAppBar()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAppBar() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_camera,
                R.id.navigation_collection,
                R.id.navigation_records
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun navigateCollectionToInfo(id: Int) {
        navController.navigate(
            CollectionFragmentDirections.actionNavigationCollectionToFragmentCollectionItemInfo(id)
        )
    }
}