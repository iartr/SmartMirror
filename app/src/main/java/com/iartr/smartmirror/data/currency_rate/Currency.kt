package com.iartr.smartmirror.data.currency_rate

enum class Currency(val description: String) {
    USD("Доллар США"),
    EUR("Евро"),
    JPY("Японская иена"),
    GBP("Фунт стерлингов"),
    AUD("Австралийский доллар"),
    CAD("Канадский доллар"),
    CHF("Швейцарский франк"),
    CNY("Китайский юань"),
    SEK("Шведская крона"),
    MXN("Мексиканский песо"),
    NZD("Новозеландский доллар"),
    SGD("Сингапурский доллар"),
    HKD("Гонгонгский доллар"),
    NOK("Норвежская крона"),
    KRW("Южнокорейская вона"),
    TRY("Турецкая лира"),
    INR("Индийская рупия"),
    BRL("Бразильский реал"),
    ZAR("Южноафриканский рэнд"),
    DKK("Датская крона"),
    PLN("Польский злотый"),
    TWD("Новый тайваньский доллар"),
    THB("Тайский бат"),
    MYR("Малайзийский ринггит"),
    RUB("Российский рубль");

    override fun toString() = name

    companion object {
        fun getCodes(currency: Currency) = values().filter { it != currency }
        fun getCodeQuery(currency: Currency): String {
            return values().filter { it != currency }.joinToString(separator = ",") { it.name }
        }
    }
}
