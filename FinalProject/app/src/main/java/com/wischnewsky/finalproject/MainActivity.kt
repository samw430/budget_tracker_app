package com.wischnewsky.finalproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        YoYo.with(Techniques.FadeIn).duration(3000L).playOn(ivSplash)

        Handler().postDelayed(
            Runnable {
                val intent = Intent()
                intent.setClass(this@MainActivity, ScrollingActivity::class.java)
                startActivity(intent)
            }, 3000
        )
    }
}
