package com.wischnewsky.finalproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed(
            Runnable {
                val intent = Intent()
                intent.setClass(this@MainActivity, ScrollingActivity::class.java)
                startActivity(intent)
            }, 3000
        )
    }
}
