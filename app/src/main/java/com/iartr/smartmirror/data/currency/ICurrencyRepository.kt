package com.iartr.smartmirror.data.currency

interface ICurrencyRepository {
    fun getCurrencyList(currencies: List<CurrencyType>): List<Currency>
}