package com.android.imhome.ui.main

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.android.imhome.util.BaseActivity
import com.android.imhome.R
import com.android.imhome.databinding.ActivityHomeBinding
import com.android.imhome.ui.myhome.MyHomeSettingActivity
import com.android.imhome.util.Util

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
        mBinding.buttonMyHomeSetting.setOnClickListener { MyHomeSettingActivity.start(this@HomeActivity) }
        mBinding.iconIsHomeStatus.isEnabled = Util.isHomed(this)
    }

    override fun onResume() {
        super.onResume()

        if (::mBinding.isInitialized){
            mBinding.iconIsHomeStatus.isEnabled = Util.isHomed(this)
        }
    }
}