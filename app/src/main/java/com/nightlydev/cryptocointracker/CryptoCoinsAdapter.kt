package com.nightlydev.cryptocointracker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by eduardo on 12/5/17.
 */
class CryptoCoinsAdapter : RecyclerView.Adapter<CryptoCoinsAdapter.CryptoCoinViewHolder>() {

    private var items: ArrayList<CryptoCoin> = ArrayList()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CryptoCoinViewHolder, position: Int) {
        holder.bindCryptoCoin(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoCoinViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_crypto_coin, parent, false)

        return CryptoCoinViewHolder(view)
    }


    class CryptoCoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindCryptoCoin(cryptoCoin: CryptoCoin) {
            TODO("Implement here code to bind a CryptoCoin")
        }
    }
}