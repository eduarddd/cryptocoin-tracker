package com.nightlydev.cryptocointracker.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nightlydev.cryptocointracker.App
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
import com.nightlydev.cryptocointracker.data.response.priceHistory
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.FavoriteCryptoCoin
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crypto_coin_detail.*
import org.reactivestreams.Subscription
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 15-12-17
 */
class CryptoCoinDetailActivity: Activity(), View.OnClickListener {
    companion object {
        val EXTRA_CRYPTO_COIN = "CRYPTO_COIN"
        val ALPHA = 40
        val DAY = 1
        val WEEK = 7
        val MONTH = 30
        val QUARTER_YEAR = 90
        val HALF_YEAR = 180
        val YEAR = 365
        val ALL = -1
        var mSelectedPeriod = WEEK
    }

    private lateinit var mCryptoCoin : CryptoCoin
    private var mFavoriteMenuItem: MenuItem? = null
    private val cryptoCoinRepository = CryptoCoinRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto_coin_detail)

        title = ""
        actionBar.setDisplayHomeAsUpEnabled(true)

        mCryptoCoin = intent.getSerializableExtra(EXTRA_CRYPTO_COIN) as CryptoCoin

        initButtons()
        bindCoinData()
        fetchCryptoCoinHistory()
        updateButtonColors(bt_one_week)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        mFavoriteMenuItem = menu?.findItem(R.id.action_save_favorite)
        updateFavoriteMenuItem()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_save_favorite) {
            saveFavorite()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        updateFavoriteMenuItem()
    }

    private fun saveFavorite() {
        App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.findFavorite(mCryptoCoin.id)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    result: FavoriteCryptoCoin? ->
                    if (result == null) {
                        App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.insert(mCryptoCoin)
                    } else {
                         App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.delete(mCryptoCoin)
                    }
                }, {
                    error -> App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.insert(mCryptoCoin)
                }
                )
    }

    private fun updateFavoriteMenuItem() {
        App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.findFavorite(mCryptoCoin.id)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    result: FavoriteCryptoCoin? ->
                    if (result == null) {
                        mFavoriteMenuItem?.setIcon(R.drawable.ic_star_border_white_24dp)
                    } else {
                        mFavoriteMenuItem?.setIcon(R.drawable.ic_star_white_24dp)
                    }
                }, {
                    error -> mFavoriteMenuItem?.setIcon(R.drawable.ic_star_border_white_24dp)
                })
    }

    override fun onClick(v : View?) {
        when(v?.id) {
            R.id.bt_one_day -> mSelectedPeriod = DAY
            R.id.bt_one_week -> mSelectedPeriod = WEEK
            R.id.bt_one_month -> mSelectedPeriod = MONTH
            R.id.bt_three_month -> mSelectedPeriod = QUARTER_YEAR
            R.id.bt_six_month -> mSelectedPeriod = HALF_YEAR
            R.id.bt_one_year -> mSelectedPeriod = YEAR
            R.id.bt_all -> mSelectedPeriod = ALL
        }
        fetchCryptoCoinHistory()
        updateButtonColors(v)
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

    private fun fetchCryptoCoinHistory() {
        progress_bar.visibility = View.VISIBLE
        cryptoCoinRepository.listCryptoCoinHistory(mSelectedPeriod, mCryptoCoin.short)
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
        val dataPoints = priceHistory.mapTo(ArrayList()) { DataPoint(it.date, it.value) }

        val array = arrayOfNulls<DataPoint>(dataPoints.size)
        val series = LineGraphSeries<DataPoint>(dataPoints.toArray(array))

        series.isDrawBackground = true

        val color = mCryptoCoin.iconColor(this)
        series.color = color
        series.backgroundColor = color and 0x00ffffff or (ALPHA shl 24)
        series.thickness = 6

        with(graph_view_history) {
            removeAllSeries()
            addSeries(series)
            setPadding(0, 0, 0 , 16)
            with(viewport) {
                isXAxisBoundsManual = true
                isYAxisBoundsManual = true
                setMinX(series.lowestValueX)
                setMaxX(series.highestValueX)
                setMinY(series.lowestValueY)
                setMaxY(series.highestValueY)
            }

            setupGridLabelRenderer(gridLabelRenderer)
            visibility = View.VISIBLE
        }
    }

    private fun setupGridLabelRenderer(gridLabelRenderer: GridLabelRenderer) {
        with(gridLabelRenderer) {
            isHorizontalLabelsVisible = true
            isVerticalLabelsVisible = false
            numVerticalLabels = 3

            when(mSelectedPeriod) {
                DAY -> {
                    labelFormatter = getCustomLabelFormatter("HH:mm")
                    numHorizontalLabels = 4
                }
                WEEK, MONTH, QUARTER_YEAR, HALF_YEAR, YEAR -> {
                    labelFormatter = getCustomLabelFormatter("dd/MM")
                    numHorizontalLabels = 4
                }
                ALL -> {
                    labelFormatter = getCustomLabelFormatter("MM/yy")
                    numHorizontalLabels = 4
                }
            }
        }
    }

    private fun getCustomLabelFormatter(format: String): DateAsXAxisLabelFormatter {
        return DateAsXAxisLabelFormatter(
                this@CryptoCoinDetailActivity,
                SimpleDateFormat(format, Locale.getDefault()))
    }

    private fun bindCoinData() {
        title = getString(R.string.cryptocoin_name_format, mCryptoCoin.long, mCryptoCoin.short)

        val numberFormat = NumberFormat.getNumberInstance()
        tv_price_usd.text = getString(R.string.price_usd_format, numberFormat.format(mCryptoCoin.price))
        bindPercentage()
    }

    private fun bindPercentage() {
        val percentage = mCryptoCoin.cap24hrChange
        val green = ContextCompat.getColor(this, R.color.green)
        val red = ContextCompat.getColor(this, R.color.red)

        if (percentage > 0) {
            tv_percent_change_24h.setTextColor(green)
        } else {
            tv_percent_change_24h.setTextColor(red)
        }
        var formattedPercentage = NumberFormat.getNumberInstance().format(percentage)

        if (percentage > 0) {
            formattedPercentage = "+" + formattedPercentage
        }
        tv_percent_change_24h.text = getString(R.string.perc_change_format, formattedPercentage)
    }

    private fun updateButtonColors(v : View?) {
        bt_one_day.setTextColor(ContextCompat.getColor(this, R.color.white))
        bt_one_month.setTextColor(ContextCompat.getColor(this, R.color.white))
        bt_one_week.setTextColor(ContextCompat.getColor(this, R.color.white))
        bt_three_month.setTextColor(ContextCompat.getColor(this, R.color.white))
        bt_six_month.setTextColor(ContextCompat.getColor(this, R.color.white))
        bt_one_year.setTextColor(ContextCompat.getColor(this, R.color.white))
        bt_all.setTextColor(ContextCompat.getColor(this, R.color.white))
        (v as TextView).setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
    }
}