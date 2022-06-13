package com.iartr.smartmirror.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.ui.weather.adapter.PagerAdapter
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.*


const val API_KEY = "7a2188082c6e48668f3181748220706"

class WeatherFragment : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentCity: String

    private lateinit var currTemp: TextView
    private lateinit var currCity: TextView
    private lateinit var currLocalTime: TextView
    private lateinit var searchButton: Button
    private lateinit var editCity: EditText
    private lateinit var iconView: ImageView
    private lateinit var infoView: TextView
    private lateinit var humidity: TextView
    private lateinit var windSpeed: TextView

    private lateinit var tabLayout: TabLayout
    private lateinit var pages: ViewPager2


    private val viewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currTemp = view.findViewById(R.id.currentTemp)
        currCity = view.findViewById(R.id.city)
        currLocalTime = view.findViewById(R.id.localtime)
        tabLayout = view.findViewById(R.id.tabLayout)
        pages = view.findViewById(R.id.placeholder)
        searchButton = view.findViewById(R.id.searchButton)
        editCity = view.findViewById(R.id.editCity)
        iconView = view.findViewById(R.id.imageTemp)
        infoView = view.findViewById(R.id.infoText)
        humidity = view.findViewById(R.id.humidity)
        windSpeed = view.findViewById(R.id.windSpeed)

        currentCity = ""
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        if(!checkPermission()){
            requestPermission()
        }
        else{
            getLastLocation()
        }

        pages.adapter = PagerAdapter(this)
        TabLayoutMediator(tabLayout, pages) { tab, index ->
            tab.text = when(index) {
                1 -> getString(R.string.week)
                else -> getString(R.string.day)
            }
        }.attach()

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {undoChanges()}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        searchButton.setOnClickListener {
            searchCity(editCity.text.toString())
        }

        viewModel.getCurrentWeather().observe(this.viewLifecycleOwner) { (it+"\u00B0").also { currTemp.text = it } }
        viewModel.getCurrentCity().observe(this.viewLifecycleOwner) { currCity.text = it }
        viewModel.getCurrentInfo().observe(this.viewLifecycleOwner) { infoView.text = it }
        viewModel.getCurrentWind().observe(this.viewLifecycleOwner) { windSpeed.text = it }
        viewModel.getCurrentHumidity().observe(this.viewLifecycleOwner) { humidity.text = it }
        viewModel.getCurrentUpdate().observe(this.viewLifecycleOwner) { currLocalTime.text = it }
        viewModel.getCurrentIcon().observe(this.viewLifecycleOwner){
            Log.d("WLog", it)
            Picasso.get().load("https:"+it).error(R.drawable.iconholder).into(iconView)
        }

        viewModel.getAvgTemp().observe(this.viewLifecycleOwner) { (it+"\u00B0").also { currTemp.text = it } }
        viewModel.getAvgInfo().observe(this.viewLifecycleOwner) { infoView.text = it }
        viewModel.getAvgWind().observe(this.viewLifecycleOwner) { windSpeed.text = it }
        viewModel.getAvgHumidity().observe(this.viewLifecycleOwner) { humidity.text = it }
        viewModel.getAvgTime().observe(this.viewLifecycleOwner) { currLocalTime.text = it }
        viewModel.getAvgIcon().observe(this.viewLifecycleOwner){
            Log.d("WLog", it)
            Picasso.get().load("https:"+it).error(R.drawable.iconholder).into(iconView)
        }
    }

    fun undoChanges(){
        (viewModel.currentTemp.value + "\u00B0").also { currTemp.text = it }
        humidity.text = viewModel.currentHumidity.value
        infoView.text = viewModel.currentInfo.value
        windSpeed.text = viewModel.currentWindSpeed.value
        currLocalTime.text = viewModel.currentTime.value
        Picasso.get().load("https:"+viewModel.currentIcon.value).error(R.drawable.iconholder).into(iconView)

    }


    private fun getCurrentResult(name: String) {
        val url = "https://api.weatherapi.com/v1/current.json?key=$API_KEY&q=$name&aqi=no&lang=ru"
        val queue = Volley.newRequestQueue(this.requireContext())
        val temp: String
        val stringRequest = VolleyUTF8EncodingStringRequest(Request.Method.GET,
            url,
            { response ->
                val obj = JSONObject(response)
                Log.d("WLog", obj.toString())
                val temp = obj.getJSONObject("current").getString("temp_c")
                val city = obj.getJSONObject("location").getString("name")
                val updateTime = obj.getJSONObject("location").getString("localtime")
                val urlIcon = obj.getJSONObject("current").getJSONObject("condition").getString("icon")
                val information = obj.getJSONObject("current").getJSONObject("condition").getString("text")
                val wind = obj.getJSONObject("current").getString("wind_mph")
                val hum = obj.getJSONObject("current").getString("humidity")

                viewModel.currentTemp.value = temp
                viewModel.currentCity.value = city
                viewModel.currentTime.value = updateTime
                viewModel.currentIcon.value = urlIcon
                viewModel.currentInfo.value = information
                viewModel.currentWindSpeed.value = wind
                viewModel.currentHumidity.value = hum
            },
            {
                Toast.makeText(this.requireContext(), getString(R.string.cityNotFound), Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }

    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        requestMultiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }


    val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {

                    } else {
                        currentCity = getCityName(location.latitude, location.longitude)
                        getCurrentResult(currentCity)
                    }
                }
            } else {
                Toast.makeText(
                    this.requireContext(),
                    getString(R.string.getLocation),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            requestPermission()
        }
    }


    private fun getCityName(lat: Double, long: Double): String {
        var cityName = ""
        var geoCoder = Geocoder(this.requireContext(), Locale.getDefault())
        var address = geoCoder.getFromLocation(lat, long, 1)
        cityName = address.get(0).locality
        return cityName
    }

    private fun searchCity(city: String) {
        if (city.isEmpty()) {
            Toast.makeText(this.requireContext(), getString(R.string.enterCityName), Toast.LENGTH_SHORT).show()
        } else {
            getCurrentResult(city)
            currentCity = city
        }
    }

    companion object{
        @JvmStatic
        fun newInstance() = WeatherFragment()
    }

}