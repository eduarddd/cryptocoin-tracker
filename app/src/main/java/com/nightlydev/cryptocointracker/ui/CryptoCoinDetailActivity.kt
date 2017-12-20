package com.nightlydev.cryptocointracker.ui

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
import com.nightlydev.cryptocointracker.data.response.priceHistory
import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crypto_coin_detail.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 15-12-17
 */
class CryptoCoinDetailActivity: Activity() {
    companion object {
        val EXTRA_CRYPTO_COIN = "CRYPTO_COIN"
        val ALPHA = 40
    }

    private lateinit var mCryptoCoin : CryptoCoin
    private val cryptoCoinRepository = CryptoCoinRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto_coin_detail)

        title = ""
        actionBar.setDisplayHomeAsUpEnabled(true)

        mCryptoCoin = intent.getSerializableExtra(EXTRA_CRYPTO_COIN) as CryptoCoin

        bindCoinData()
        fetchCryptoCoinHistory(7)
    }

    private fun fetchCryptoCoinHistory(dayCount: Int) {
        progress_bar.visibility = View.VISIBLE
        cryptoCoinRepository.listCryptoCoinHistory(dayCount, mCryptoCoin.symbol)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result ->
                        progress_bar.visibility = View.GONE
                        tv_history_error.visibility = View.GONE
                        displayPriceHistoryInfo(result.priceHistory())
                }, {
                    error ->
                        progress_bar.visibility = View.GONE
                        tv_history_error.visibility = View.VISIBLE
                        error.printStackTrace()
                })
    }

    private fun displayPriceHistoryInfo(priceHistory: List<CryptoCoinHistoryPriceItem>) {
        val dataPoints = ArrayList<DataPoint>()
        val minX = priceHistory[0].date.time.toDouble()
        val maxX = priceHistory[priceHistory.size -1].date.time.toDouble()
        var minY = priceHistory[0].value
        var maxY = priceHistory[0].value

        for (priceItem in priceHistory) {
            minY = Math.min(minY, priceItem.value)
            maxY = Math.max(maxY, priceItem.value)

            dataPoints.add(DataPoint(priceItem.date, priceItem.value))
        }

        val array = arrayOfNulls<DataPoint>(dataPoints.size)
        val series = LineGraphSeries<DataPoint>(dataPoints.toArray(array))
        series.isDrawBackground = true

        val color = mCryptoCoin.iconColor(this)
        series.color = color
        series.backgroundColor = color and 0x00ffffff or (ALPHA shl 24)
        series.thickness = 4

        with(graph_view_history) {
            addSeries(series)
            with(viewport) {
                isXAxisBoundsManual = true
                isYAxisBoundsManual = true
                setMinX(minX)
                setMaxX(maxX)
                setMinY(minY)
                setMaxY(maxY)
            }
            with(gridLabelRenderer) {
                isHorizontalLabelsVisible = true
                isVerticalLabelsVisible = true
                setPadding(0, 0, 0 , 8)
                labelFormatter = DateAsXAxisLabelFormatter(
                        context,
                        SimpleDateFormat("dd/MM", Locale.getDefault()))
            }
            visibility = View.VISIBLE
        }
    }

    private fun bindCoinData() {
        val color = mCryptoCoin.iconColor(this)
        actionBar.setBackgroundDrawable(ColorDrawable(color))
        val alpha = 180
        window.statusBarColor = color and 0x00ffffff or (alpha shl 24)

        title = getString(R.string.cryptocoin_name_format, mCryptoCoin.name, mCryptoCoin.symbol)

        val numberFormat = NumberFormat.getNumberInstance()
        tv_price_usd.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.price_usd))
        tv_market_cap.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.market_cap_usd))
        tv_total_supply.text = numberFormat.format(mCryptoCoin.total_supply)

        bindPercentages()
    }

    private fun bindPercentages() {
        bindPercentage(mCryptoCoin.percent_change_1h, tv_percent_change_1h)
        bindPercentage(mCryptoCoin.percent_change_24h, tv_percent_change_24h)
        bindPercentage(mCryptoCoin.percent_change_7d, tv_percent_change_7d)
    }

    private fun bindPercentage(percentage: Double, percentageTextView: TextView) {
        val green = ContextCompat.getColor(this, R.color.green)
        val red = ContextCompat.getColor(this, R.color.red)

        if (percentage > 0) {
            percentageTextView.setTextColor(green)
        } else {
            percentageTextView.setTextColor(red)
        }
        percentageTextView.text = NumberFormat.getNumberInstance().format(percentage) + "%"
    }

}