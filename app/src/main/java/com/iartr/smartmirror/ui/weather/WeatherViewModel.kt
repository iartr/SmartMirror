package com.iartr.smartmirror.ui.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.Volley

class WeatherViewModel: ViewModel() {

    public val currentTemp = MutableLiveData<String>()
    public val currentCity = MutableLiveData<String>()
    public val updateTime = MutableLiveData<String>()

    public fun getCurrentWeather() : LiveData<String>{
        return currentTemp
    }

    public fun getCurrentCity() : LiveData<String>{
        return currentCity
    }
    public fun getCurrentUpdate() : LiveData<String>{
        return updateTime
    }
}