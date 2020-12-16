package com.goldouble.android.laundryday

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        checkPermission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    }

    private fun checkPermission(vararg permissions: String) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsValue(false)) finish()
            else Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(application, MainActivity::class.java))
                finish()
            }, 1500)
        }.launch(permissions)
    }
}