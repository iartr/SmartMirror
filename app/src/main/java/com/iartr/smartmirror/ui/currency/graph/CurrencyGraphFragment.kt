package com.iartr.smartmirror.ui.currency.graph

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.currency_rate.Currency
import com.iartr.smartmirror.data.currency_rate.CurrencyRateRepository
import com.iartr.smartmirror.data.currency_rate.currencyRateApi
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.ui.currency.graph.CurrencyGraphViewModel.CurrencyRatesState
import com.iartr.smartmirror.utils.RetryingErrorView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class CurrencyGraphFragment : BaseFragment(R.layout.fragment_currency_graph) {

    private lateinit var graph: GraphView
    private lateinit var spinner: TextInputLayout
    private lateinit var loader: ProgressBar
    private lateinit var error: RetryingErrorView

    private val viewModel: CurrencyGraphViewModel by viewModels(
        factoryProducer = {
            CurrencyGraphViewModel.Factory(CurrencyRateRepository(currencyRateApi))
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields(view)
        initObservers()
    }

    private fun initFields(view: View) {
        graph = view.findViewById(R.id.graph)
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true
        spinner = view.findViewById(R.id.spinner)
        val currencies = Currency.getCodes(Currency.RUB)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, currencies)
        (spinner.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        spinner.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.getTimeseriesCurrencyRates(Currency.valueOf(text.toString()))
        }
        loader = view.findViewById(R.id.loader)
        error = view.findViewById(R.id.error)
    }

    private fun initObservers() {
        viewModel.currencyRatesState.subscribeWithFragment(::applyCurrencyRatesState)
    }

    private fun applyCurrencyRatesState(currencyRatesState: CurrencyRatesState) {
        graph.isVisible = currencyRatesState is CurrencyRatesState.Success ||
                currencyRatesState is CurrencyRatesState.Nothing
        loader.isVisible = currencyRatesState is CurrencyRatesState.Loading
        when (currencyRatesState) {
            is CurrencyRatesState.Success -> {
                val rates = currencyRatesState.rates
                val dataPoints = (rates.indices).map { DataPoint(it.toDouble(), rates[it]) }
                val series = LineGraphSeries(dataPoints.toTypedArray())
                graph.series.clear()
                graph.addSeries(series)
            }
            is CurrencyRatesState.Error -> {
                error.show(
                    retryAction = { viewModel.getTimeseriesCurrencyRates(currencyRatesState.currency) }
                )
            }
            else -> {}
        }
    }
}