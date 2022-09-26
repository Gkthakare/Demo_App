package com.example.registrationscreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.registrationscreen.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation)

        binding.appName.animation = topAnim

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreen,LoginActivity::class.java)

            val pair = Pair<View,String>(binding.appName,"splash_text")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashScreen,pair)
            startActivity(intent,options.toBundle())

        },4000)

    }
}