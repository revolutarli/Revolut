package com.challenge.revolut

import com.challenge.revolut.features.exchange.view.ViewState
import com.challenge.revolut.network.exchange.CurrencyExchangeRemoteModel

val remoteModel = CurrencyExchangeRemoteModel(
    baseCurrency = "EUR",
    rates = listOf(
        CurrencyExchangeRemoteModel.Money("AUS", 10.0),
        CurrencyExchangeRemoteModel.Money("USD", 20.0)
    )
)

val EurBaseViewState = ViewState(
    base = ViewState.Money(1,"EUR", 1.0),
    rates = listOf(
        ViewState.Money(1,"EUR", 1.0),
        ViewState.Money(1,"AUS", 10.0),
        ViewState.Money(2, "USD", 20.0)
    ),
    loading = false,
    error = null
)
val EurBaseTimes100ViewState = ViewState(
    base = ViewState.Money(1,"EUR", 100.0),
    rates = listOf(
        ViewState.Money(1,"EUR", 100.0),
        ViewState.Money(1,"AUS", 1000.0),
        ViewState.Money(2, "USD", 2000.0)
    ),
    loading = false,
    error = null
)

val AusBaseViewState = ViewState(
    base = ViewState.Money(1,"AUS", 10.0),
    rates = listOf(
        ViewState.Money(1,"AUS", 10.0),
        ViewState.Money(1, "USD", 200.0)
    ),
    loading = false,
    error = null
)

val errorViewState = ViewState(loading = false, error = "Opps an error has occurred")