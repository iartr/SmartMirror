package com.iartr.smartmirror.ui.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.weather.adapter.DayAdapter
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DayTempFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val adapter = DayAdapter()

    private val viewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daytemp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.dayView)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerView.adapter = adapter

        viewModel.getCurrentCity().observe(viewLifecycleOwner){
            getDayForecast(it.toString())
            Log.d("WLog", it)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDayForecast(name: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY&q=$name&days=1&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(this.requireContext())
        val stringRequest = VolleyUTF8EncodingStringRequest(
            Request.Method.GET,
            url,
            { response ->
                val obj = JSONObject(response)
                val time = obj.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0)
                val hours = time.getJSONArray("hour")
                val localTime = obj.getJSONObject("location").getString("localtime")
                adapter.clear()
                val sdf = SimpleDateFormat(getString(R.string.hourMinute))
                val sdfWeather = SimpleDateFormat(getString(R.string.fullTimeWeather))
                for (i in 0 until hours.length()){
                    val currentTime = sdf.format(sdfWeather.parse(localTime) as Date)

                    val hourJson = sdfWeather.parse(hours.getJSONObject(i).getString("time"))
                    val hour = sdf.format(hourJson as Date)
                    val temp = hours.getJSONObject(i).getString("temp_c")
                    val icon = hours.getJSONObject(i).getJSONObject("condition").getString("icon")
                    if(currentTime<=hour){
                        val day = DayTemp(hour, temp, icon)
                        adapter.addDayTemp(day)
                    }

                }

            },
            {})
        queue.add(stringRequest)
    }

    companion object{
        @JvmStatic
        fun newInstance() = DayTempFragment()
    }
}