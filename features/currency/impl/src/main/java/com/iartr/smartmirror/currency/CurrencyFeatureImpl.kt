package com.iartr.smartmirror.currency

import com.iartr.smartmirror.network.retrofitApi

class CurrencyFeatureImpl : CurrencyFeatureApi {
    override fun repository(): ICurrencyRepository {
        return CurrencyRepository(
            api = retrofitApi("https://api.coinbase.com/v2/")
        )
    }
}