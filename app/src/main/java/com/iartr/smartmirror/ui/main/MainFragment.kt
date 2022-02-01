package com.iartr.smartmirror.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.coord.CoordRepository
import com.iartr.smartmirror.data.coord.coordApi
import com.iartr.smartmirror.data.weather.WeatherRepository
import com.iartr.smartmirror.data.weather.weatherApi
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.utils.RetryingErrorView
import com.iartr.smartmirror.utils.subscribeSuccess

class MainFragment : BaseFragment(R.layout.fragment_main) {
    private lateinit var accountButton: ImageView
    private lateinit var weatherContainer: View
    private lateinit var weatherData: TextView
    private lateinit var weatherLoading: ProgressBar
    private lateinit var weatherError: RetryingErrorView

    private val viewModel: MainViewModel by viewModels(
        factoryProducer = {
            MainViewModel.Factory(
                weatherRepository = WeatherRepository(
                    api = weatherApi,
                    coordRepository = CoordRepository(coordApi = coordApi)
                )
            )
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountButton = view.findViewById(R.id.main_account_button)
        weatherContainer = view.findViewById(R.id.main_weather_container)
        weatherData = view.findViewById(R.id.main_weather_container_data)
        weatherLoading = view.findViewById(R.id.main_weather_container_loader)
        weatherError = view.findViewById(R.id.main_weather_container_error)

        viewModel.state
            .subscribeWithFragment { weatherState ->
                when (weatherState) {
                    is MainViewModel.WeatherState.Success -> {
                        weatherLoading.isVisible = false
                        weatherError.hide()
                        weatherData.isVisible = true
                        weatherData.text = weatherState.temperature
                    }
                    is MainViewModel.WeatherState.Error -> {
                        weatherLoading.isVisible = false
                        weatherError.show(retryAction = { viewModel.getWeather() })
                        weatherData.isVisible = false
                    }
                    is MainViewModel.WeatherState.Loading -> {
                        weatherLoading.isVisible = true
                        weatherError.hide()
                        weatherData.isVisible = false
                    }
                }
            }
    }
}