package com.challenge.revolut

import com.challenge.revolut.features.exchange.model.ExchangeModel
import com.challenge.revolut.features.exchange.model.ExchangeModelImpl
import com.challenge.revolut.network.ApiInterface
import com.challenge.revolut.network.exchange.CurrencyExchangeRemoteModel
import com.challenge.revolut.shared.SchedulerProvider
import com.nhaarman.mockitokotlin2.internal.createInstance
import io.reactivex.schedulers.TestScheduler
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Before
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit

import org.junit.Test

class ExchangeModelTest {

    lateinit var tested: ExchangeModel

    lateinit var api: ApiInterface
    lateinit var schedulerProvider: SchedulerProvider
    lateinit var testScheduler: TestScheduler

    @Before
    fun setup() {
        api = mock()
        schedulerProvider = mock()

        testScheduler = TestScheduler()
        whenever(schedulerProvider.io()).thenReturn(testScheduler)
        whenever(schedulerProvider.computation()).thenReturn(testScheduler)

        whenever(api.currencies(any())).thenReturn(Observable.just(remoteModel))

        tested = ExchangeModelImpl(api, schedulerProvider)
    }

    @Test
    fun shouldGetCurrencies() {
        val testObserver = tested.currencies("EUR").test()

        testObserver.assertNotTerminated()
            .assertNoErrors()
            .assertValueCount(0)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserver.assertValue(remoteModel)
    }
}

inline fun <reified T : Any> any() = any(T::class.java) ?: createInstance()
