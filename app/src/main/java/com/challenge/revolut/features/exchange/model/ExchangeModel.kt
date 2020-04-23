package com.challenge.revolut.features.exchange.model

import com.challenge.revolut.network.ApiInterface
import com.challenge.revolut.network.exchange.CurrencyExchangeRemoteModel
import com.challenge.revolut.shared.SchedulerProvider
import io.reactivex.Observable

interface ExchangeModel {
    fun currencies(baseCurrency: String): Observable<CurrencyExchangeRemoteModel>
}

class ExchangeModelImpl(
    private val api: ApiInterface,
    private val schedulerProvider: SchedulerProvider
) : ExchangeModel {
    override fun currencies(baseCurrency: String): Observable<CurrencyExchangeRemoteModel> =
        api.currencies(baseCurrency)
            .subscribeOn(schedulerProvider.io())
}