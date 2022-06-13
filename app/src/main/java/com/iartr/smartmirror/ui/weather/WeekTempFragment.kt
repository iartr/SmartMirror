package com.iartr.smartmirror.ui.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.weather.adapter.WeekAdapter
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class WeekTempFragment: Fragment(), WeekAdapter.Listener {

    private lateinit var recyclerView: RecyclerView
    private val adapter = WeekAdapter(this)

    private val viewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weektemp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.weekView)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerView.adapter = adapter


        viewModel.getCurrentCity().observe(viewLifecycleOwner){
            getWeekForecast(it.toString())
            Log.d("WLog", it)
        }

    }


    @SuppressLint("SimpleDateFormat")
    private fun getWeekForecast(name: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY&q=$name&days=7&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(this.requireContext())
        val stringRequest = VolleyUTF8EncodingStringRequest(
            Request.Method.GET,
            url,
            { response ->
                val obj = JSONObject(response)
                val day = obj.getJSONObject("forecast").getJSONArray("forecastday")
                adapter.clear()
                val sdf = SimpleDateFormat(getString(R.string.reverseDateWeather))
                val sdfWeather = SimpleDateFormat(getString(R.string.dateWeather))
                for(i in 0 until day.length()){
                    val date = day.getJSONObject(i).getString("date")
                    val formatDate = sdf.format(sdfWeather.parse(date) as Date)
                    val maxTemp = day.getJSONObject(i).getJSONObject("day").getString("maxtemp_c")
                    val minTemp = day.getJSONObject(i).getJSONObject("day").getString("mintemp_c")
                    val avgTemp = day.getJSONObject(i).getJSONObject("day").getString("avgtemp_c")
                    val icon = day.getJSONObject(i).getJSONObject("day").getJSONObject("condition").getString("icon")
                    val info = day.getJSONObject(i).getJSONObject("day").getJSONObject("condition").getString("text")
                    val humidity = day.getJSONObject(i).getJSONObject("day").getString("avghumidity")
                    val windSpeed = day.getJSONObject(i).getJSONObject("day").getString("maxwind_mph")
                    val week = WeekTemp(formatDate, "$minTemp-$maxTemp",avgTemp, icon, info, windSpeed, humidity)
                    adapter.addWeekTemp(week)
                }
            },
            {})
        queue.add(stringRequest)
    }
    companion object{
        @JvmStatic
        fun newInstance() = WeekTempFragment()
    }

    override fun onClick(weekTemp: WeekTemp) {
        viewModel.avgTime.value = weekTemp.date
        viewModel.avgIcon.value = weekTemp.icon
        viewModel.avgInfo.value = weekTemp.info
        viewModel.avgHumidity.value = weekTemp.humidity
        viewModel.avgWindSpeed.value = weekTemp.windSpeed
        viewModel.avgTemp.value = weekTemp.avgTemp
    }
}