package com.iartr.smartmirror.ui.currency.graph

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.TextView
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
import com.iartr.smartmirror.ui.currency.util.asDate
import com.iartr.smartmirror.utils.RetryingErrorView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*

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
        graph.viewport.isScrollable = true
        graph.viewport.isScalable = true
        spinner = view.findViewById(R.id.spinner)
        val currencies = Currency.getCodes(Currency.RUB)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, currencies)
        (spinner.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        spinner.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.getTimeseriesCurrencyRates(Currency.valueOf(text.toString()))
        }
        (spinner.editText as? AutoCompleteTextView)?.setText(Currency.USD.toString(), false)
        loader = view.findViewById(R.id.loader)
        error = view.findViewById(R.id.error)
    }

    private fun initObservers() {
        viewModel.currencyRatesState.subscribeWithFragment(::applyCurrencyRatesState)
    }

    private fun applyCurrencyRatesState(currencyRatesState: CurrencyRatesState) {
        graph.isVisible = currencyRatesState is CurrencyRatesState.Success
        loader.isVisible = currencyRatesState is CurrencyRatesState.Loading
        when (currencyRatesState) {
            is CurrencyRatesState.Success -> {
                val dataPoints = currencyRatesState.dataPoints
                val series = LineGraphSeries(dataPoints.toTypedArray())
                graph.removeAllSeries()
                graph.addSeries(series)

                graph.gridLabelRenderer.numHorizontalLabels = 3
                graph.gridLabelRenderer.numVerticalLabels = 6
                graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(requireActivity())

                graph.viewport.setMinY(dataPoints.minOf { it.y })
                graph.viewport.setMaxY(dataPoints.maxOf { it.y })

                graph.viewport.setMinX(dataPoints.minOf { it.x })
                graph.viewport.setMaxX(dataPoints.maxOf { it.x })
                graph.viewport.isXAxisBoundsManual = true
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