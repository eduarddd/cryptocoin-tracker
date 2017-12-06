package com.nightlydev.cryptocointracker

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_crypto_coin.view.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class CryptoCoinsAdapter : RecyclerView.Adapter<CryptoCoinsAdapter.CryptoCoinViewHolder>() {

    private var mItems: ArrayList<CryptoCoin>

    init {
        mItems = ArrayList()
    }

    override fun getItemCount(): Int = mItems.size

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

    fun setItems(cryptoCoinList: List<CryptoCoin>) {
        mItems = ArrayList(cryptoCoinList)
        notifyDataSetChanged()
    }

    class CryptoCoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: CryptoIconTextView = itemView.tv_icon
        private val rank: TextView = itemView.tv_rank
        private val symbol: TextView = itemView.tv_symbol
        private val name: TextView = itemView.tv_name
        private val priceUsd: TextView = itemView.tv_price_usd
        private val percentage24h: TextView = itemView.tv_percent_change_24h
        private val percentage7d: TextView = itemView.tv_percent_change_7d

        fun bindCryptoCoin(coin: CryptoCoin) {
            bindIcon(coin)
            rank.text = coin.rank.toString()
            symbol.text = coin.symbol
            name.text = coin.name
            priceUsd.text = itemView.context.getString(R.string.price_usd_regex, coin.price_usd)
            bindPercentageChanges(coin)
        }

        private fun bindIcon(coin: CryptoCoin) {
            var iconRestId = itemView.resources.getIdentifier(coin.symbol, "string", itemView.context.packageName)
            var iconColorId = itemView.resources.getIdentifier(coin.symbol, "color", itemView.context.packageName)

            if (iconRestId <= 0) {
                iconRestId = itemView.resources.getIdentifier(coin.name, "string", itemView.context.packageName)
            }
            if (iconRestId > 0) {
                icon.text = itemView.context.getString(iconRestId)
            }

            if (iconColorId <= 0) {
                iconColorId = itemView.resources.getIdentifier(coin.name, "color", itemView.context.packageName)
            }
            if (iconColorId > 0) {
                icon.setTextColor(ContextCompat.getColor(itemView.context, iconColorId))
            }
        }

        private fun bindPercentageChanges(coin: CryptoCoin) {
            val percentageChange24h = coin.percent_change_24h
            val percentageChange7d = coin.percent_change_7d


            val green = ContextCompat.getColor(itemView.context, R.color.green)
            val red = ContextCompat.getColor(itemView.context, R.color.red)

            if (percentageChange24h > 0) {
                percentage24h.setTextColor(green)
            } else {
                percentage24h.setTextColor(red)
            }
            percentage24h.text = percentageChange24h.toString()

            if (percentageChange7d > 0) {
                percentage7d.setTextColor(green)
            } else {
                percentage7d.setTextColor(red)
            }
            percentage7d.text = percentageChange7d.toString()
        }

        fun resetViews() {
            icon.text = ""
            rank.text = ""
            name.text = ""
            priceUsd.text= ""
            percentage24h.text = ""
            percentage7d.text = ""
        }
    }
}