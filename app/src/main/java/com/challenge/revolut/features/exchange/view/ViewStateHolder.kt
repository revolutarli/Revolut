package com.challenge.revolut.features.exchange.view

data class ViewState(
    val base: Money = Money(
        currency = "EUR",
        value = 1.0
    ),
    val rates: List<Money> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
) {
    data class Money(
        val position: Int = -1,
        val currency: String,
        val value: Double
    )
}

class ViewStateHolder {
    private var viewState =
        ViewState()

    val state get() = viewState

    val base get() = viewState.base

    fun setBase(position: Int? = null, currency: String? = null, value: Double? = null) {
        viewState = viewState.copy(
            base = base.copy(
                position = position ?: viewState.base.position,
                currency = currency ?: viewState.base.currency,
                value = value ?: viewState.base.value
            )
        )
    }

    fun success(
        base: ViewState.Money,
        rates: List<ViewState.Money>
    ) = viewState.copy(
        base = base,
        rates = rates,
        loading = false
    ).also { viewState = it }

    fun error(error: String) = ViewState(
        error = error,
        loading = false
    ).also { viewState = it }
}