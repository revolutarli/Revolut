package com.challenge.revolut.network.exchange

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class MoneyJsonConverter : JsonDeserializer<CurrencyExchangeRemoteModel> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CurrencyExchangeRemoteModel {
        val currency = json?.asJsonObject?.run {
            val baseCurrency = this.get("baseCurrency").asString
            val jsonRates = this.get("rates").asJsonObject

            val rates = jsonRates.keySet().map { currency ->
                CurrencyExchangeRemoteModel.Money(currency, jsonRates.get(currency).asDouble)
            }

            CurrencyExchangeRemoteModel(baseCurrency, rates)
        }

        return currency ?: CurrencyExchangeRemoteModel("", emptyList())
    }
}