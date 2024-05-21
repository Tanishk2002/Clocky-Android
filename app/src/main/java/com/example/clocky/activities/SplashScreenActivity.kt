package com.example.clocky.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.clocky.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1200)

        installSplashScreen().apply {
            initialize()
        }
    }

    private fun initialize(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}