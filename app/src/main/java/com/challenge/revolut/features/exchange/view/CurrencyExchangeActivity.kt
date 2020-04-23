package com.challenge.revolut.features.exchange.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.challenge.revolut.R
import com.challenge.revolut.features.exchange.model.ExchangeModelImpl
import com.challenge.revolut.network.NetworkManager
import com.challenge.revolut.shared.SchedulerProviderImpl
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*


class CurrencyExchangeActivity : AppCompatActivity() {
    private lateinit var viewRatesAdapter: MoneyRatesAdapter

    private val schedulerProvider = SchedulerProviderImpl()
    private val disposable = CompositeDisposable()

    private val viewModel =
        ExchangeViewModelImpl(
            ExchangeModelImpl(
                NetworkManager().repository,
                schedulerProvider
            ),
            schedulerProvider
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewRatesAdapter = MoneyRatesAdapter(
            ViewState(), viewModel::setValue
        ) { base ->
            currencyRecyclerView.post {
                currencyRecyclerView.scrollToPosition(0)
            }
            viewModel.fetch(base)
        }
        viewRatesAdapter.setHasStableIds(true)

        currencyRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = viewRatesAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        disposable.add(subscribeToViewState())
    }

    override fun onPause() {
        disposable.clear()
        super.onPause()
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun subscribeToViewState(): Disposable {
        return viewModel.listen()
            .observeOn(schedulerProvider.ui())
            .subscribe({ updateView(it) }) {
                Log.e("Error", it.message ?: "error")
            }
    }

    private fun updateView(viewState: ViewState) {
        when {
            viewState.loading -> {
                progressBar.visibility = View.VISIBLE
                currencyRecyclerView.visibility = View.GONE
                error.visibility = View.GONE
            }
            viewState.error == null -> {
                error.visibility = View.GONE
                progressBar.visibility = View.GONE

                currencyRecyclerView.visibility = View.VISIBLE
                viewRatesAdapter.updateData(viewState)
            }
            else -> {
                progressBar.visibility = View.GONE
                currencyRecyclerView.visibility = View.GONE

                error.visibility = View.VISIBLE
                error.text = viewState.error
            }
        }
    }
}
