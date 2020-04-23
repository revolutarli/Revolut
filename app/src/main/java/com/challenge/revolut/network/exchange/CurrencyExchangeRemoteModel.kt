package com.challenge.revolut.network.exchange

data class CurrencyExchangeRemoteModel(
    val baseCurrency: String,
    val rates: List<Money>
) {
    data class Money(
        val currency: String,
        val value: Double
    )
}

