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
class CryptoCoinDetailActivity: Activity(), View.OnClickListener {
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

        initButtons()
        bindCoinData()
        fetchCryptoCoinHistory(1)
        bt_one_day.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun initButtons() {
        bt_one_day.setOnClickListener(this)
        bt_one_month.setOnClickListener(this)
        bt_one_week.setOnClickListener(this)
        bt_three_month.setOnClickListener(this)
        bt_six_month.setOnClickListener(this)
        bt_one_year.setOnClickListener(this)
        bt_all.setOnClickListener(this)
    }

    override fun onClick(v : View?) {
        when(v?.id) {
            R.id.bt_one_day -> fetchCryptoCoinHistory(1)
            R.id.bt_one_week -> fetchCryptoCoinHistory(7)
            R.id.bt_one_month -> fetchCryptoCoinHistory(30)
            R.id.bt_three_month -> fetchCryptoCoinHistory(90)
            R.id.bt_six_month -> fetchCryptoCoinHistory(180)
            R.id.bt_one_year -> fetchCryptoCoinHistory(365)
            R.id.bt_all -> fetchCryptoCoinHistory(0)
        }
        updateButtonColors(v)
    }

    private fun updateButtonColors(v : View?) {
        bt_one_day.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        bt_one_month.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        bt_one_week.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        bt_three_month.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        bt_six_month.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        bt_one_year.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        bt_all.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        (v as TextView).setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun fetchCryptoCoinHistory(dayCount: Int) {
        progress_bar.visibility = View.VISIBLE
        cryptoCoinRepository.listCryptoCoinHistory(dayCount, mCryptoCoin.short)
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
            removeAllSeries()
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
                isVerticalLabelsVisible = false
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

        title = getString(R.string.cryptocoin_name_format, mCryptoCoin.long, mCryptoCoin.short)

        val numberFormat = NumberFormat.getNumberInstance()
        tv_price_usd.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.price))
        val marketCapText = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.mktcap))
        tv_market_cap.text = getString(R.string.label_market_cap, marketCapText)
        val supplyText = numberFormat.format(mCryptoCoin.supply)
        tv_total_supply.text = getString(R.string.label_total_supply, supplyText)

        bindPercentages()
    }

    private fun bindPercentages() {
        bindPercentage(mCryptoCoin.cap24hrChange, tv_percent_change_24h)
    }

    private fun bindPercentage(percentage: Double, percentageTextView: TextView) {
        val green = ContextCompat.getColor(this, R.color.green)
        val red = ContextCompat.getColor(this, R.color.red)

        if (percentage > 0) {
            percentageTextView.setTextColor(green)
        } else {
            percentageTextView.setTextColor(red)
        }
        var formattedPercentage = NumberFormat.getNumberInstance().format(percentage)

        if (percentage > 0) {
            formattedPercentage = "+" + formattedPercentage
        }
        percentageTextView.text = getString(R.string.perc_change_format, formattedPercentage)
    }

}