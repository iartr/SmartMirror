package com.iartr.smartmirror.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.coord.CoordRepository
import com.iartr.smartmirror.data.coord.coordApi
import com.iartr.smartmirror.data.currency.CurrencyRepository
import com.iartr.smartmirror.data.currency.currencyApi
import com.iartr.smartmirror.data.weather.WeatherRepository
import com.iartr.smartmirror.data.weather.weatherApi
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.utils.RetryingErrorView

class MainFragment : BaseFragment(R.layout.fragment_main) {
    private lateinit var accountButton: ImageView
    private lateinit var weatherContainer: View
    private lateinit var weatherData: TextView
    private lateinit var weatherLoading: ProgressBar
    private lateinit var weatherError: RetryingErrorView

    private lateinit var currencyList: LinearLayout
    private lateinit var rubToUsd: TextView
    private lateinit var rubToEur: TextView
    private lateinit var currencyLoader: ProgressBar
    private lateinit var currencyError: RetryingErrorView

    private val viewModel: MainViewModel by viewModels(
        factoryProducer = {
            MainViewModel.Factory(
                weatherRepository = WeatherRepository(
                    api = weatherApi,
                    coordRepository = CoordRepository(coordApi = coordApi)
                ),
                currencyRepository = CurrencyRepository(api = currencyApi)
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
        currencyList = view.findViewById(R.id.main_currency_container_list)
        rubToUsd = view.findViewById(R.id.main_rub_to_usd_currency)
        rubToEur = view.findViewById(R.id.main_rub_to_eur_currency)
        currencyLoader = view.findViewById(R.id.main_currency_container_loader)
        currencyError = view.findViewById(R.id.main_currency_container_error)

        viewModel.weatherState.subscribeWithFragment(::applyWeatherState)
        viewModel.currencyState.subscribeWithFragment(::applyCurrencyState)
    }

    private fun applyWeatherState(weatherState: MainViewModel.WeatherState) =
        when (weatherState) {
            is MainViewModel.WeatherState.Success -> {
                weatherLoading.isVisible = false
                weatherError.hide()
                weatherData.isVisible = true
                weatherData.text = weatherState.temperature
            }
            is MainViewModel.WeatherState.Error -> {
                weatherLoading.isVisible = false
                weatherError.show(retryAction = { viewModel.loadWeather() })
                weatherData.isVisible = false
            }
            is MainViewModel.WeatherState.Loading -> {
                weatherLoading.isVisible = true
                weatherError.hide()
                weatherData.isVisible = false
            }
        }

    private fun applyCurrencyState(currencyState: MainViewModel.CurrencyState) {
        when (currencyState) {
            is MainViewModel.CurrencyState.Success -> {
                currencyList.isVisible = true
                currencyLoader.isVisible = false
                currencyError.hide()

                rubToUsd.text = getString(R.string.main_currency_usd, currencyState.exchangeRates.usdRate)
                rubToEur.text = getString(R.string.main_currency_eur, currencyState.exchangeRates.eurRate)
            }
            is MainViewModel.CurrencyState.Loading -> {
                currencyList.isVisible = false
                currencyLoader.isVisible = true
                currencyError.hide()
            }
            is MainViewModel.CurrencyState.Error -> {
                currencyList.isVisible = false
                currencyLoader.isVisible = false
                currencyError.show(retryAction = { viewModel.loadCurrency() })
            }
        }
    }
}