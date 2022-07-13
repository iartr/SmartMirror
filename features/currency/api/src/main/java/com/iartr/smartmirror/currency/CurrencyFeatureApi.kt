package com.iartr.smartmirror.currency

lateinit var currencyFeatureApiProvider: Lazy<CurrencyFeatureApi>

interface CurrencyFeatureApi {
    fun repository(): ICurrencyRepository
}