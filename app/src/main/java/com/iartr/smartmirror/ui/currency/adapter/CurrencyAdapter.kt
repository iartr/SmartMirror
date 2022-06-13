package com.iartr.smartmirror.ui.currency.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iartr.smartmirror.R
import com.iartr.smartmirror.data.currency_rate.CurrencyRate

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    private var items: List<CurrencyRate> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(value: List<CurrencyRate>) {
        items = value
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
    )

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvValue: TextView = itemView.findViewById(R.id.tvValue)

        fun bind(item: CurrencyRate) {
            tvName.text = String.format("%s: %s", item.currency.name, item.currency.description)
            tvValue.text = String.format("%.4f", item.rate)
        }
    }
}