package com.nightlydev.cryptocointracker.cryptoCoinDetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.data.ViewModelFactory
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.ui.iconColor
import kotlinx.android.synthetic.main.activity_crypto_coin_detail.*
import kotlinx.android.synthetic.main.toolbar_top.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 15-12-17
 */
class CryptoCoinDetailActivity: AppCompatActivity(), View.OnClickListener {
    companion object {
        val EXTRA_CRYPTO_COIN_ID = "CRYPTO_COIN_ID"
        val STATE_CRYPTO_COIN_ID = "CRYPTO_COIN_ID"
        val STATE_PERIOD = "STATE_PERIOD"
        val ALPHA = 40
        val DAY = 1
        val WEEK = 7
        val MONTH = 30
        val QUARTER_YEAR = 90
        val HALF_YEAR = 180
        val YEAR = 365
        val ALL = -1
        val DEFAULT_PERIOD  = DAY
    }

    private var mCryptoCoinViewModel: CryptoCoinViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto_coin_detail)

        setSupportActionBar(toolbar)
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        initButtons()

        if (savedInstanceState != null) {
            val cryptoCoinId = savedInstanceState.getString(STATE_CRYPTO_COIN_ID)
            val period = savedInstanceState.getInt(STATE_PERIOD)
            initViewModel(cryptoCoinId, period)
        } else {
            val cryptoCoinId = intent.getStringExtra(EXTRA_CRYPTO_COIN_ID)
            val period = DEFAULT_PERIOD
            initViewModel(cryptoCoinId, period)
        }
    }

    private fun initViewModel(cryptoCoinId: String, period: Int) {
        val viewModelFactory = ViewModelFactory(cryptoCoinId, period)
        mCryptoCoinViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(CryptoCoinViewModel::class.java)

        mCryptoCoinViewModel
                ?.getCryptoCoin()
                ?.observe(this, Observer<CryptoCoin> { coin ->
                    bindCoinData(coin)
                    subscribeToCryptoCoinHistory()
                })
    }

    private fun subscribeToCryptoCoinHistory() {
        mCryptoCoinViewModel?.getCryptoCoinHistory()?.observe(
                this,
                Observer { priceHistory ->
                    progress_bar.visibility = View.GONE

                    if (priceHistory != null) {
                        tv_history_error.visibility = View.GONE
                        displayPriceHistoryInfo(priceHistory)
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        setupFavoriteMenuItem(menu.findItem(R.id.action_save_favorite))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save_favorite -> {
            saveFavorite()
            true
        }
        android.R.id.home -> { finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState?.putString(STATE_CRYPTO_COIN_ID, mCryptoCoinViewModel?.getCryptoCoin()?.value?.shortName!!)
        outState?.putInt(STATE_PERIOD, mCryptoCoinViewModel?.getDisplayHistoryPeriod()?.value!!)
    }

    private fun saveFavorite() = mCryptoCoinViewModel?.saveFavorite()

    private fun setupFavoriteMenuItem(favoriteMenuItem: MenuItem) {
        mCryptoCoinViewModel?.isFavorite()?.observe(this, Observer<Boolean> { isFavorite ->
            if (isFavorite!!) {
                favoriteMenuItem.setIcon(R.drawable.ic_star_white_24dp)
            } else {
                favoriteMenuItem.setIcon(R.drawable.ic_star_border_white_24dp)
            }
        })
    }

    override fun onClick(v : View?) {
        var selectedPeriod : Int? = null
        when(v?.id) {
            R.id.bt_one_day -> selectedPeriod = DAY
            R.id.bt_one_week -> selectedPeriod = WEEK
            R.id.bt_one_month -> selectedPeriod = MONTH
            R.id.bt_three_month -> selectedPeriod = QUARTER_YEAR
            R.id.bt_six_month -> selectedPeriod = HALF_YEAR
            R.id.bt_one_year -> selectedPeriod = YEAR
            R.id.bt_all -> selectedPeriod = ALL
        }
        fetchCryptoCoinHistory(selectedPeriod!!)
        updateButtonColors(v)
    }

    private fun fetchCryptoCoinHistory(displayHistoryPeriod: Int) {
        progress_bar.visibility = View.VISIBLE
        mCryptoCoinViewModel?.getDisplayHistoryPeriod()?.value = displayHistoryPeriod
    }

    private fun displayPriceHistoryInfo(priceHistory: List<CryptoCoinHistoryPriceItem>?) {
        val dataPoints = priceHistory?.mapTo(ArrayList()) { DataPoint(it.date, it.value) }

        val array = arrayOfNulls<DataPoint>(dataPoints!!.size)
        val series = LineGraphSeries<DataPoint>(dataPoints.toArray(array))

        series.isDrawBackground = true

        val cryptoCoin = mCryptoCoinViewModel?.getCryptoCoin()?.value
        val color = cryptoCoin!!.iconColor(this)
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
        mCryptoCoinViewModel?.getDisplayHistoryPeriod()?.observe(
                this,
                Observer<Int> { displayHistoryPeriod ->
                    with(gridLabelRenderer) {
                        isHorizontalLabelsVisible = true
                        isVerticalLabelsVisible = false
                        numVerticalLabels = 3

                        when(displayHistoryPeriod) {
                            DAY -> {
                                labelFormatter = getCustomLabelFormatter("HH:mm")
                                numHorizontalLabels = 6
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
        )
    }

    private fun getCustomLabelFormatter(format: String): DateAsXAxisLabelFormatter {
        return DateAsXAxisLabelFormatter(
                this@CryptoCoinDetailActivity,
                SimpleDateFormat(format, Locale.getDefault()))
    }

    private fun bindCoinData(cryptoCoin: CryptoCoin?) {
        title = getString(R.string.cryptocoin_name_format, cryptoCoin?.longName, cryptoCoin?.shortName)

        val numberFormat = NumberFormat.getNumberInstance()
        tv_price_usd.text = getString(R.string.price_usd_format, numberFormat.format(cryptoCoin?.price))
        bindPercentage(cryptoCoin)
    }

    private fun bindPercentage(cryptoCoin: CryptoCoin?) {
        val percentage = cryptoCoin?.cap24hrChange ?: return
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

    private fun initButtons() {
        bt_one_day.setOnClickListener(this)
        bt_one_month.setOnClickListener(this)
        bt_one_week.setOnClickListener(this)
        bt_three_month.setOnClickListener(this)
        bt_six_month.setOnClickListener(this)
        bt_one_year.setOnClickListener(this)
        bt_all.setOnClickListener(this)
        updateButtonColors(bt_one_day)
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