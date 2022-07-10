package com.iartr.smartmirror.currency.api

import com.iartr.smartmirror.currency.CurrencyApi
import com.iartr.smartmirror.currency.CurrencyRepository
import com.iartr.smartmirror.currency.ICurrencyRepository
import com.iartr.smartmirror.network.retrofitApi

class CurrencyFeatureApi {
    fun repository(): ICurrencyRepository {
        return CurrencyRepository(
            api = network()
        )
    }

    private fun network(): CurrencyApi {
        return retrofitApi("https://api.coinbase.com/v2/")
    }
}