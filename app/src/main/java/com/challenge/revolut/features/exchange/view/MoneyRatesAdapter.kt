package com.challenge.revolut.features.exchange.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.challenge.revolut.R
import java.text.DecimalFormat
import java.util.*

class MoneyRatesAdapter(
    private var viewState: ViewState,
    val setValue: (Double) -> Unit,
    val setCurrency: (ViewState.Money) -> Unit
) :
    RecyclerView.Adapter<MoneyRatesAdapter.CurrencyViewHolder>() {

    private val numberFormatter = DecimalFormat("#.##")

    @SuppressLint("ClickableViewAccessibility")
    inner class CurrencyViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.currencyIcon)
        val title: TextView = view.findViewById(R.id.currencyTitle)
        val name: TextView = view.findViewById(R.id.currencyName)
        val value: EditText = view.findViewById(R.id.currencyValue)

        init {
            value.doAfterTextChanged { text ->
                money?.let { money ->
                    val selected = viewState.base.currency == money.currency
                    if (selected) {
                        val value = try {
                            text.toString().toDouble()
                        } catch (e: NumberFormatException) {
                            0.0
                        }

                        setValue(value)
                    }
                }
            }

            view.setOnClickListener {
                money?.let(setCurrency)
            }

            value.setOnTouchListener { _, event ->
                money?.let { money ->
                    if (MotionEvent.ACTION_UP == event.action) {
                        setCurrency(money)
                    }
                }
                return@setOnTouchListener false
            }
        }

        private val money
            get(): ViewState.Money? {
                return if (adapterPosition != -1 || adapterPosition < viewState.rates.size) {
                    viewState.rates[adapterPosition]
                } else {
                    null
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.currency, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val resources = holder.name.resources

        viewState.rates[position].let { money ->
            holder.title.text = money.currency
            holder.name.text = resources.getString(
                resources.getIdentifier(
                    money.currency,
                    "string",
                    holder.name.context.packageName
                )
            )

            val selected = viewState.base.currency == money.currency
            if (selected) {
                holder.value.setText(numberFormatter.format(viewState.base.value))
                holder.value.post { holder.value.requestFocus() }
            } else {
                holder.value.setText(numberFormatter.format(money.value))
            }

            val icon = resources.getIdentifier(
                "mipmap/${money.currency.toLowerCase(Locale.ROOT)}",
                null,
                holder.icon.context.packageName
            )
            holder.icon.setImageDrawable(ContextCompat.getDrawable(holder.icon.context, icon))

        }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = viewState.rates.size

    fun updateData(data: ViewState) {
        val currentBase = data.base
        val previousBase = viewState.base

        when {
            currentBase.currency != previousBase.currency -> {
                viewState = data.copy(rates = viewState.rates
                    .swap(currentBase.position, 0)
                    .apply {
                        removeAt(currentBase.position)
                        add(previousBase.position, previousBase)
                    }
                )

                notifyItemChanged(currentBase.position, 0)
                notifyItemChanged(0)
            }
            data.rates.size != viewState.rates.size -> {
                viewState = data
                notifyDataSetChanged()
            }
            else -> {
                viewState = data
                notifyItemRangeChanged(1, viewState.rates.size - 1)
            }
        }
    }
}

fun List<ViewState.Money>.swap(index1: Int, index2: Int): MutableList<ViewState.Money> {
    val tempList = this.toMutableList()
    Collections.swap(tempList, index1, index2)
    return tempList
}