package com.nightlydev.cryptocointracker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_crypto_coin.view.*

/**
 * Created by eduardo on 12/5/17.
 */
class CryptoCoinsAdapter : RecyclerView.Adapter<CryptoCoinsAdapter.CryptoCoinViewHolder>() {

    private var mItems: ArrayList<CryptoCoin>

    init {
        mItems = ArrayList()
    }

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: CryptoCoinViewHolder, position: Int) {
        val cryptoCoin = mItems[position]
        holder.resetViews()

        holder.rank.text = cryptoCoin.rank.toString()
        holder.name.text = cryptoCoin.name
        holder.priceUsd.text = holder.itemView.context.getString(R.string.price_usd_regex, cryptoCoin.price_usd.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoCoinViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_crypto_coin, parent, false)

        return CryptoCoinViewHolder(view)
    }

    fun add(cryptoCoin: CryptoCoin) {
        mItems.add(cryptoCoin)
        notifyItemInserted(mItems.indexOf(cryptoCoin))
    }

    class CryptoCoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rank: TextView = itemView.tv_rank
        val name: TextView = itemView.tv_name
        val priceUsd: TextView = itemView.tv_price_usd

        fun resetViews() {
            rank.text = ""
            name.text = ""
            priceUsd.text= ""
        }
    }
}