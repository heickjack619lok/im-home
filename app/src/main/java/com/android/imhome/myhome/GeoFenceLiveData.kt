package com.android.imhome.myhome

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class GeoFenceLiveData : ViewModel() {
    companion object {
        private val isHome = MutableLiveData<Boolean>()

        fun setIsHome(isHome: Boolean) {
            this.isHome.postValue(isHome)
        }

        fun getIsHome(): MutableLiveData<Boolean> {
            return isHome
        }
    }
}