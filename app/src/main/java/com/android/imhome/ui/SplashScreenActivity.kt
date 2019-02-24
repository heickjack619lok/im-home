package com.android.imhome.ui

import android.os.Bundle
import android.os.Handler
import com.android.imhome.util.BaseActivity
import com.android.imhome.R
import com.android.imhome.ui.main.HomeActivity

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val runnable = Runnable {
            HomeActivity.start(this)
            finish()
        }
        Handler().postDelayed(runnable, 500)
    }
}
