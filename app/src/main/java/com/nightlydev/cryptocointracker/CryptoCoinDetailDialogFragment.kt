package com.nightlydev.cryptocointracker

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.fragmen_crypto_coin_detail.view.*
import java.text.NumberFormat

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 13-12-17
 */
class CryptoCoinDetailDialogFragment : DialogFragment() {
    companion object {
        private val ARG_CRYPTO_COIN = "CRYPTO_COIN"

        @JvmStatic
        fun newInstance(cryptoCoin: CryptoCoin): CryptoCoinDetailDialogFragment {
            return CryptoCoinDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CRYPTO_COIN, cryptoCoin)
                }
            }
        }
    }

    private lateinit var mCryptoCoin : CryptoCoin

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mCryptoCoin = arguments.getSerializable(ARG_CRYPTO_COIN) as CryptoCoin
        val view = activity.layoutInflater.inflate(R.layout.fragmen_crypto_coin_detail, null)

        bindCoinData(view)
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setView(view)
        alertDialogBuilder.setNegativeButton(R.string.close) { p0, p1 -> dismiss() }

        return alertDialogBuilder.create()
    }

    private fun bindCoinData(view: View) {
        bindIcon(view)
        view.tv_name.text = getString(R.string.cryptocoin_name_format, mCryptoCoin.name, mCryptoCoin.symbol)

        val numberFormat = NumberFormat.getNumberInstance()
        view.tv_price_usd.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.price_usd))
        view.tv_market_cap.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.market_cap_usd))
        view.tv_total_supply.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.total_supply))

        bindPercentages(view)
    }

    private fun bindIcon(view: View) {
        val iconRestId = mCryptoCoin.icon(activity)
        val iconColorId = mCryptoCoin.iconColor(activity)

        if (iconRestId > 0) {
            view.tv_icon.text = activity.getString(iconRestId)
        }
        if (iconColorId > 0) {
            view.tv_icon.setTextColor(ContextCompat.getColor(activity, iconColorId))
        }
    }

    private fun bindPercentages(view: View) {
        bindPercentage(mCryptoCoin.percent_change_1h, view.tv_percent_change_1h)
        bindPercentage(mCryptoCoin.percent_change_24h, view.tv_percent_change_24h)
        bindPercentage(mCryptoCoin.percent_change_7d, view.tv_percent_change_7d)
    }

    private fun bindPercentage(percentage: Double, percentageTextView: TextView) {
        val green = ContextCompat.getColor(activity, R.color.green)
        val red = ContextCompat.getColor(activity, R.color.red)

        if (percentage > 0) {
            percentageTextView.setTextColor(green)
        } else {
            percentageTextView.setTextColor(red)
        }
        percentageTextView.text = NumberFormat.getNumberInstance().format(percentage) + "%"
    }
}