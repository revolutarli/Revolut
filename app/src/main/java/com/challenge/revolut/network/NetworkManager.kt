package com.challenge.revolut.network

import com.challenge.revolut.network.exchange.CurrencyExchangeRemoteModel
import com.challenge.revolut.network.exchange.MoneyJsonConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkManager {

    companion object {
        const val BASE_URL = "https://hiring.revolut.codes/api/android/"
    }

    val repository
        get() = restService(retrofit(httpClient, gson, rxJavaAdapter))

    private val httpClient
        get() =
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

    private val gson
        get() : Gson = GsonBuilder().registerTypeAdapter(
            CurrencyExchangeRemoteModel::class.java, MoneyJsonConverter()
        ).create()

    private val rxJavaAdapter
        get() = RxJava2CallAdapterFactory.create()

    private fun retrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        rxJavaCallAdapterFactory: RxJava2CallAdapterFactory
    ): Retrofit.Builder {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(rxJavaCallAdapterFactory)
            .client(okHttpClient)
    }

    private fun restService(
        retrofitBuilder: Retrofit.Builder
    ): ApiInterface {
        return retrofitBuilder.baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)
    }
}