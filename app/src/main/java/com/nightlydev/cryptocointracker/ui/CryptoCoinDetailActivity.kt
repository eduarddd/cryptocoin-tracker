package com.nightlydev.cryptocointracker.ui

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.icon
import com.nightlydev.cryptocointracker.model.iconColor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crypto_coin_detail.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by edu on 15-12-17.
 */
class CryptoCoinDetailActivity: Activity() {
    companion object {
        val EXTRA_CRYPTO_COIN = "CRYPTO_COIN"
        val ALPHA = 40
    }

    private lateinit var mCryptoCoin : CryptoCoin
    private lateinit var mGraphView: GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto_coin_detail)

        mCryptoCoin = intent.getSerializableExtra(EXTRA_CRYPTO_COIN) as CryptoCoin

        title = ""
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setBackgroundDrawable(ColorDrawable(resources.getColor(mCryptoCoin.iconColor(this))))
        mGraphView = graph
        bindCoinData()
        fetchCryptoCoinHistory(7)
    }

    private fun fetchCryptoCoinHistory(dayCount: Int) {
        progress_bar.visibility = View.VISIBLE
        CryptoCoinRepository().listCryptoCoinHistory(dayCount, mCryptoCoin.symbol)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result ->
                        progress_bar.visibility = View.GONE
                        displayPriceHistoryInfo(result.price)
                }, {
                    error ->
                        progress_bar.visibility = View.GONE
                        error.printStackTrace()
                })
    }

    private fun displayPriceHistoryInfo(priceHistory: List<List<Any>>) {
        val dataPoints = ArrayList<DataPoint>()
        val minX = priceHistory[0][0] as Double
        val maxX = priceHistory[priceHistory.size -1][0] as Double

        var minY = priceHistory[0][1] as Double
        var maxY = priceHistory[0][1] as Double

        for (priceItem: List<Any> in priceHistory) {
            val dateValue = priceItem[0] as Double
            val priceValue = priceItem[1] as Double
            val date = SimpleDateFormat.getDateInstance().format(Date(dateValue.toLong()))
            val price = NumberFormat.getNumberInstance().format(priceValue)


            minY = Math.min(minY, priceValue)
            maxY = Math.max(maxY, priceValue)
            dataPoints.add(DataPoint(Date(dateValue.toLong()), priceValue))
        }

        val array = arrayOfNulls<DataPoint>(dataPoints.size)
        val series = LineGraphSeries<DataPoint>(dataPoints.toArray(array))
        series.isDrawBackground = true

        val color = ContextCompat.getColor(this, mCryptoCoin.iconColor(this))
        series.color = color
        series.backgroundColor = color and 0x00ffffff or (ALPHA shl 24)
        series.thickness = 4

        with(mGraphView) {
            addSeries(series)
            with(viewport) {
                isXAxisBoundsManual = true
                isYAxisBoundsManual = true
                setMinX(minX)
                setMaxX(maxX)

                setMinY(minY)
                setMaxY(maxY)

                isScalable = true
                isScrollable = true
                setScalableY(true)
                setScrollableY(true)
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
        bindIcon()
        tv_name.text = getString(R.string.cryptocoin_name_format, mCryptoCoin.name, mCryptoCoin.symbol)

        val numberFormat = NumberFormat.getNumberInstance()
        tv_price_usd.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.price_usd))
        tv_market_cap.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.market_cap_usd))
        tv_total_supply.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.total_supply))

        bindPercentages()
    }

    private fun bindIcon() {
        val iconStringResId = mCryptoCoin.icon(this)
        val iconColorResId = mCryptoCoin.iconColor(this)

        if (iconStringResId > 0) tv_icon.text = getString(iconStringResId)
        if (iconColorResId > 0) tv_icon.setTextColor(ContextCompat.getColor(this, iconColorResId))
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