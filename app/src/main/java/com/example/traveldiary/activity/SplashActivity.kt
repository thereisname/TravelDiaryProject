package com.example.traveldiary.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.traveldiary.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val img: ImageView = findViewById(R.id.splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val options = ActivityOptions.makeSceneTransitionAnimation(this, img, "imageTran")
            val intent = Intent(this, StartViewActivity::class.java)
            startActivity(intent, options.toBundle())
        }, 3000) // 시간 0.5초 이후 실행
    }

    override fun onStop() {
        super.onStop()
        finish()
        window.setWindowAnimations(0)
    }
}