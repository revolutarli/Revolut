package com.challenge.revolut.network

import com.challenge.revolut.network.exchange.CurrencyExchangeRemoteModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("latest")
    fun currencies(@Query("base") latest: String): Observable<CurrencyExchangeRemoteModel>
}