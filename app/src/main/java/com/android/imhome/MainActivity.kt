package com.android.imhome

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val runnable = Runnable {
            HomeActivity.start(this)
            finish()
        }
        Handler().postDelayed(runnable, 500)
    }
}
