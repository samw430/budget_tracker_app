package com.wischnewsky.budgettrackerapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            Runnable {
                val intent = Intent()
                intent.setClass(this@SplashActivity, ScrollingActivity::class.java)
                startActivity(intent)
            }, 3000
        )
    }
}
