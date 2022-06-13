package com.iartr.smartmirror.ui.currency.list

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.currency_rate.CurrencyRateRepository
import com.iartr.smartmirror.data.currency_rate.currencyRateApi
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.ui.currency.adapter.CurrencyAdapter
import com.iartr.smartmirror.ui.currency.list.CurrencyListViewModel.CurrencyRatesState
import com.iartr.smartmirror.ui.currency.util.VerticalSpaceItemDecoration
import com.iartr.smartmirror.utils.RetryingErrorView

class CurrencyListFragment : BaseFragment(R.layout.fragment_currency_list) {

    private val adapter =  CurrencyAdapter()
    private lateinit var recycler: RecyclerView
    private lateinit var loader: ProgressBar
    private lateinit var error: RetryingErrorView

    private val viewModel: CurrencyListViewModel by viewModels(
        factoryProducer = {
            CurrencyListViewModel.Factory(CurrencyRateRepository(currencyRateApi))
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields(view)
        initData()
        initObservers()
    }

    private fun initFields(view: View) {
        recycler = view.findViewById<RecyclerView?>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CurrencyListFragment.adapter
            addItemDecoration(VerticalSpaceItemDecoration(resources.getDimension(R.dimen.vertical_space).toInt()))
        }
        loader = view.findViewById(R.id.loader)
        error = view.findViewById(R.id.error)
    }

    private fun initData() {
        viewModel.getLatestCurrencyRates()
    }

    private fun initObservers() {
        viewModel.currencyRatesState.subscribeWithFragment(::applyCurrencyRatesState)
    }

    private fun applyCurrencyRatesState(currencyRatesState: CurrencyRatesState) {
        recycler.isVisible = currencyRatesState is CurrencyRatesState.Success
        loader.isVisible = currencyRatesState is CurrencyRatesState.Loading
        when (currencyRatesState) {
            is CurrencyRatesState.Success -> {
                adapter.submitList(currencyRatesState.currencyRates)
            }
            CurrencyRatesState.Error -> {
                error.show(retryAction = { viewModel.getLatestCurrencyRates() })
            }
            else -> {}
        }
    }
}