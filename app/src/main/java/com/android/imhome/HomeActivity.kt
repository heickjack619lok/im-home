package com.android.imhome

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.android.imhome.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity(){

    private lateinit var mBinding:ActivityHomeBinding

    companion object {
        fun start(context: Context){
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    }
}