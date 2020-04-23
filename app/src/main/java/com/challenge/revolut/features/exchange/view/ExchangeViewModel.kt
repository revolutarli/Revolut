package com.challenge.revolut.features.exchange.view

import com.challenge.revolut.features.exchange.model.ExchangeModel
import com.challenge.revolut.network.exchange.CurrencyExchangeRemoteModel
import com.challenge.revolut.shared.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.Observable.interval
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface ExchangeViewModel {
    fun listen(): Observable<ViewState>

    fun fetch(base: ViewState.Money)

    fun setValue(value: Double)
}

class ExchangeViewModelImpl(
    private val exchangeModel: ExchangeModel,
    private val schedulerProvider: SchedulerProvider
) : ExchangeViewModel {

    private val stateChanges = PublishSubject.create<ViewState>()
    private val state = ViewStateHolder()

    override fun listen(): Observable<ViewState> {
        return combineLatest(
            fetchExchangeRates(),
            stateChanges.startWith(ViewState()),
            BiFunction { exchange: CurrencyExchangeRemoteModel, viewState: ViewState ->
                exchange to combineViewStateWithRates(viewState, exchange.rates)
            })
            .map { (exchange, viewState) ->
                viewState.base.let { base ->
                    val exchangeRates = exchange.rates.associate { Pair(it.currency, it.value) }
                    state.success(
                        base,
                        listOf(base) + filterBaseFromRates(base, exchange.rates)
                            .mapIndexed { index, money ->
                                val value = exchangeRates.getValue(money.currency)

                                ViewState.Money(
                                    index + 1,
                                    money.currency,
                                    if (base.currency == money.currency) value
                                    else value * base.value
                                )
                            }
                    )
                }
            }
            .onErrorReturn {  state.error("Opps an error has occurred") }
    }

    private fun fetchExchangeRates(): Observable<CurrencyExchangeRemoteModel> =
        interval(0, 1, TimeUnit.SECONDS, schedulerProvider.io())
            .flatMap { exchangeModel.currencies(state.base.currency) }

    private fun filterBaseFromRates(
        base: ViewState.Money,
        rates: List<CurrencyExchangeRemoteModel.Money>
    ) = rates.filter { money -> base.currency != money.currency }

    private fun combineViewStateWithRates(
        viewState: ViewState,
        rates: List<CurrencyExchangeRemoteModel.Money>
    ): ViewState {
        return if (viewState.base.position == -1) {
            viewState.copy(
                base = viewState.base.copy(
                    position = getBaseSortedPosition(viewState.base, rates)
                )
            )
        } else viewState
    }

    private fun getBaseSortedPosition(
        base: ViewState.Money,
        rates: List<CurrencyExchangeRemoteModel.Money>
    ) = (listOf(base.toRemoteModel()) + rates)
        .sortedBy { money -> money.currency }
        .indexOfFirst { it.currency == base.currency }


    override fun fetch(base: ViewState.Money) {
        state.setBase(position = base.position, currency = base.currency, value = base.value)
        stateChanges.onNext(state.state)
    }

    override fun setValue(value: Double) {
        state.setBase(value = value)
        stateChanges.onNext(state.state)
    }
}

private fun ViewState.Money.toRemoteModel() = CurrencyExchangeRemoteModel.Money(
    this.currency,
    this.value
)