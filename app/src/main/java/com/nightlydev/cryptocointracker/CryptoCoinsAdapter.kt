package com.nightlydev.cryptocointracker

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_crypto_coin.view.*
import java.text.NumberFormat

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class CryptoCoinsAdapter(clickHandler: OnClickHandler) : RecyclerView.Adapter<CryptoCoinsAdapter.CryptoCoinViewHolder>() {

    private var mItems: ArrayList<CryptoCoin>
    private var mClickHandler = clickHandler

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

    interface OnClickHandler {
        fun onClick(cryptoCoin: CryptoCoin)
    }

    inner class CryptoCoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val icon = itemView.tv_icon
        private val rank = itemView.tv_rank
        private val name = itemView.tv_name
        private val priceUsd = itemView.tv_price_usd
        private val percentage24h = itemView.tv_percent_change_24h
        private val percentage7d = itemView.tv_percent_change_7d

        override fun onClick(view: View?) {
            mClickHandler.onClick(mItems[adapterPosition])
        }

        fun bindCryptoCoin(coin: CryptoCoin) {
            bindIcon(coin)
            rank.text = coin.rank.toString()
            name.text = itemView.context.getString(R.string.cryptocoin_name_format, coin.name, coin.symbol)
            val formattedPrice = NumberFormat.getNumberInstance().format(coin.price_usd)
            priceUsd.text = itemView.context.getString(R.string.price_usd_format, formattedPrice)
            bindPercentageChanges(coin)
            itemView.setOnClickListener(this)
        }

        private fun bindIcon(coin: CryptoCoin) {
            var iconRestId = coin.icon(itemView.context)
            var iconColorId = coin.iconColor(itemView.context)

            if (iconRestId > 0) {
                icon.text = itemView.context.getString(iconRestId)
            }
            if (iconColorId > 0) {
                icon.setTextColor(ContextCompat.getColor(itemView.context, iconColorId))
            }
        }

        @SuppressLint("SetTextI18n")
        private fun bindPercentageChanges(coin: CryptoCoin) {
            var percentageChange24h = coin.percent_change_24h
            var percentageChange7d = coin.percent_change_7d


            val green = ContextCompat.getColor(itemView.context, R.color.green)
            val red = ContextCompat.getColor(itemView.context, R.color.red)

            if (percentageChange24h > 0) {
                percentage24h.setTextColor(green)
            } else {
                percentage24h.setTextColor(red)
            }
            percentage24h.text = NumberFormat.getNumberInstance().format(percentageChange24h) + "%"

            if (percentageChange7d > 0) {
                percentage7d.setTextColor(green)
            } else {
                percentage7d.setTextColor(red)
            }
            percentage7d.text = NumberFormat.getNumberInstance().format(percentageChange7d) + "%"
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