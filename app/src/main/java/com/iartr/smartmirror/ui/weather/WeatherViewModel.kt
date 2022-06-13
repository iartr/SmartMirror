package com.iartr.smartmirror.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel: ViewModel() {


    //Current Temp Values
    public val currentTemp = MutableLiveData<String>()
    public val currentCity = MutableLiveData<String>()
    public val currentTime = MutableLiveData<String>()
    public val currentIcon = MutableLiveData<String>()
    public val currentInfo = MutableLiveData<String>()
    public val currentWindSpeed = MutableLiveData<String>()
    public val currentHumidity = MutableLiveData<String>()

    //Week Temp Values
    public val avgTemp = MutableLiveData<String>()
    public val avgTime = MutableLiveData<String>()
    public val avgIcon = MutableLiveData<String>()
    public val avgInfo = MutableLiveData<String>()
    public val avgWindSpeed = MutableLiveData<String>()
    public val avgHumidity = MutableLiveData<String>()

    public fun getCurrentWeather() : LiveData<String>{
        return currentTemp
    }

    public fun getCurrentCity() : LiveData<String>{
        return currentCity
    }
    public fun getCurrentUpdate() : LiveData<String>{
        return currentTime
    }
    public fun getCurrentIcon() : LiveData<String>{
        return currentIcon
    }
    public fun getCurrentInfo() : LiveData<String>{
        return currentInfo
    }
    public fun getCurrentWind() : LiveData<String>{
        return currentWindSpeed
    }
    public fun getCurrentHumidity() : LiveData<String>{
        return currentHumidity
    }


    public fun getAvgTemp() : LiveData<String>{
        return avgTemp
    }
    public fun getAvgTime() : LiveData<String>{
        return avgTime
    }

    public fun getAvgIcon() : LiveData<String>{
        return avgIcon
    }
    public fun getAvgInfo() : LiveData<String>{
        return avgInfo
    }
    public fun getAvgWind() : LiveData<String>{
        return avgWindSpeed
    }
    public fun getAvgHumidity() : LiveData<String>{
        return avgHumidity
    }
}