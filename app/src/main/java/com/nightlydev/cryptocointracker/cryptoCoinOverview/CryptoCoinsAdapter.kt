package com.nightlydev.cryptocointracker.cryptoCoinOverview

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.model.CryptoCoin
import kotlinx.android.synthetic.main.item_crypto_coin.view.*
import java.text.NumberFormat

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class CryptoCoinsAdapter
    : RecyclerView.Adapter<CryptoCoinsAdapter.CryptoCoinViewHolder>(),
        Filterable {

    var cryptoCoins = ArrayList<CryptoCoin>()
        set(value) {
            field = value
            mItems = value
            notifyDataSetChanged()
        }

    private var mItems = ArrayList<CryptoCoin>()
    var clickHandler: ((CryptoCoin) -> Unit)? = null

    override fun getItemCount(): Int = mItems.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filteredData: ArrayList<CryptoCoin> = filterCryptoCoins(query?.toString())

                return FilterResults().apply {
                    values = filteredData
                    count = filteredData.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    mItems = results.values as ArrayList<CryptoCoin>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CryptoCoinViewHolder, position: Int) {
        val coin = mItems[position]
        holder.resetViews()
        holder.bindCryptoCoin(coin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoCoinViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_crypto_coin, parent, false)

        return CryptoCoinViewHolder(view)
    }

    private fun filterCryptoCoins(filter: String?): ArrayList<CryptoCoin> {
        val result = ArrayList<CryptoCoin>()

        if (TextUtils.isEmpty(filter)) return cryptoCoins

        cryptoCoins.forEach {cryptoCoin ->
            if (cryptoCoin.symbol.contains(filter!!, true)
                    || cryptoCoin.name.contains(filter, true)) {
                result.add(cryptoCoin)
            }
        }
        return result
    }

    inner class CryptoCoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val icon = itemView.tv_icon
        private val rank = itemView.tv_rank
        private val name = itemView.tv_name
        private val priceUsd = itemView.tv_price_usd
        private val percentage24h = itemView.tv_percent_change_24h

        override fun onClick(view: View?) {
            clickHandler?.invoke(mItems[adapterPosition])
        }

        fun bindCryptoCoin(coin: CryptoCoin) {
            rank.text = (adapterPosition + 1).toString()
            icon.setCoin(coin)
            //rank.text = coin.rank.toString()
            name.text = itemView.context.getString(R.string.cryptocoin_name_format, coin.name, coin.symbol)
            val formattedPrice = NumberFormat.getNumberInstance().format(coin.price)
            priceUsd.text = itemView.context.getString(R.string.price_usd_format, formattedPrice)
            bindPercentageChanges(coin)
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        private fun bindPercentageChanges(coin: CryptoCoin) {
            val percentageChange24h = coin.cap24hrChange
            val green = ContextCompat.getColor(itemView.context, R.color.green)
            val red = ContextCompat.getColor(itemView.context, R.color.red)

            if (percentageChange24h > 0) {
                percentage24h.setTextColor(green)
            } else {
                percentage24h.setTextColor(red)
            }
            var formattedPercentage = NumberFormat.getNumberInstance().format(percentageChange24h) + "%"

            if (percentageChange24h > 0) {
                formattedPercentage = "+" + formattedPercentage
            }
            percentage24h.text = formattedPercentage
        }

        fun resetViews() {
            icon.text = ""
            rank.text = ""
            name.text = ""
            priceUsd.text= ""
            percentage24h.text = ""
        }
    }
}