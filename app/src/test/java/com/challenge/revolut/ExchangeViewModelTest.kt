package com.challenge.revolut

import com.challenge.revolut.features.exchange.model.ExchangeModel
import com.challenge.revolut.features.exchange.view.ExchangeViewModel
import com.challenge.revolut.features.exchange.view.ExchangeViewModelImpl
import com.challenge.revolut.features.exchange.view.ViewState
import com.challenge.revolut.shared.SchedulerProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import java.lang.Exception
import java.util.concurrent.TimeUnit

class ExchangeViewModelTest {

    lateinit var tested: ExchangeViewModel

    lateinit var exchangeModel: ExchangeModel
    lateinit var schedulerProvider: SchedulerProvider
    lateinit var testScheduler: TestScheduler

    @Before
    fun setup() {
        exchangeModel = mock()
        schedulerProvider = mock()

        testScheduler = TestScheduler()
        whenever(schedulerProvider.io()).thenReturn(testScheduler)

        tested = ExchangeViewModelImpl(exchangeModel, schedulerProvider)
    }

    @Test
    fun shouldGetRatesEverySecond() {
        whenever(exchangeModel.currencies(any())).thenReturn(Observable.just(remoteModel))

        val testObserver = tested.listen().test()

        testObserver.assertNotTerminated()
            .assertNoErrors()
            .assertValueCount(0)

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        testObserver.assertValues(EurBaseViewState, EurBaseViewState, EurBaseViewState)
    }


    @Test
    fun shouldEmitViewStateErrorWhenModelErrors() {
        whenever(exchangeModel.currencies(any())).thenReturn(Observable.error(Exception("Im an error")))
        val testObserver = tested.listen().test()

        testObserver.assertNotTerminated()
            .assertNoErrors()
            .assertValueCount(0)


        testScheduler.advanceTimeBy(0, TimeUnit.SECONDS)
        testObserver.assertValue(errorViewState)
    }

    @Test
    fun shouldSetBase(){
        whenever(exchangeModel.currencies(any())).thenReturn(Observable.just(remoteModel))

        val testObserver = tested.listen().test()

        testObserver.assertNotTerminated()
            .assertNoErrors()
            .assertValueCount(0)

        val base = ViewState.Money(1, "AUS", 10.0)
        tested.fetch(base)

        testScheduler.advanceTimeBy(0, TimeUnit.SECONDS)
        testObserver.assertValues(AusBaseViewState)
    }

    @Test
    fun shouldSetMoneyValue(){
        whenever(exchangeModel.currencies(any())).thenReturn(Observable.just(remoteModel))

        val testObserver = tested.listen().test()

        testObserver.assertNotTerminated()
            .assertNoErrors()
            .assertValueCount(0)

        tested.setValue(100.0)

        testScheduler.advanceTimeBy(0, TimeUnit.SECONDS)
        testObserver.assertValues(EurBaseTimes100ViewState)
    }
}