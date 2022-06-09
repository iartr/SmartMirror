package com.iartr.smartmirror.ui.weather

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.iartr.smartmirror.R
import org.json.JSONObject

const val API_KEY = "7a2188082c6e48668f3181748220706"
class WeatherActivity : AppCompatActivity() {

    private lateinit var currTemp: TextView
    private lateinit var currCity: TextView
    private lateinit var currUpdateTime: TextView

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        currTemp = findViewById(R.id.currentTemp)
        currCity = findViewById(R.id.city)
        currUpdateTime = findViewById(R.id.dateUpdate)

        supportFragmentManager.beginTransaction().replace(R.id.placeholder, DayTempFragment.newInstance()).commit()

        getResult("Perm")

        viewModel.getCurrentWeather().observe(this, Observer {
            currTemp.text = it
        })

        viewModel.getCurrentCity().observe(this, Observer {
            currCity.text = it
        })
        viewModel.getCurrentUpdate().observe(this, Observer {
            currUpdateTime.text = it
        })
    }

    private fun getResult(name : String){
        val url = "https://api.weatherapi.com/v1/current.json?key=$API_KEY&q=$name&aqi=no"
        val queue = Volley.newRequestQueue(this)
        val temp : String
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            Response.Listener{ response ->
                val obj = JSONObject(response)
                val temp = obj.getJSONObject("current").getString("temp_c")
                val city = obj.getJSONObject("location").getString("name")
                val updateTime = obj.getJSONObject("location").getString("localtime")
                viewModel.currentTemp.value = temp
                viewModel.currentCity.value = city
                viewModel.updateTime.value = updateTime
            },
            {})
        queue.add(stringRequest)
    }
}