package com.nightlydev.cryptocointracker.cryptoCoinDetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.alerts.CreateAlertDialogFragment
import com.nightlydev.cryptocointracker.data.Status
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
class CryptoCoinDetailActivity: AppCompatActivity() {

    private lateinit var mCryptoCoinViewModel: CryptoCoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto_coin_detail)
        setSupportActionBar(toolbar)
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        initButtons()
        initViewModel(savedInstanceState)
    }

    private fun initViewModel(savedInstanceState: Bundle?) {
        val cryptoCoinId = savedInstanceState?.getString(STATE_CRYPTO_COIN_ID)
                ?: intent.getStringExtra(EXTRA_CRYPTO_COIN_ID)
        val period = savedInstanceState?.getInt(STATE_PERIOD) ?: DEFAULT_PERIOD
        val viewModelFactory = ViewModelFactory(cryptoCoinId, period)

        mCryptoCoinViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(CryptoCoinViewModel::class.java)

        mCryptoCoinViewModel.cryptoCoin.observe(this, Observer { coin ->
            bindCoinData(coin)
        })

        mCryptoCoinViewModel.cryptoCoinHistory.observe(this, Observer { priceHistory ->
            progress_bar.visibility = GONE
            tv_history_error.visibility = GONE
            when (priceHistory?.status) {
                Status.LOADING -> progress_bar.visibility = VISIBLE
                Status.SUCCESS -> displayPriceHistoryInfo(priceHistory.data)
                Status.ERROR -> tv_history_error.visibility = VISIBLE
            }
        })
        updateButtonColors(period)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        setupFavoriteMenuItem(menu.findItem(R.id.action_save_favorite))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save_favorite -> {
                saveFavorite()
                true
            }
            R.id.action_add_alert -> {
                createAlert()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createAlert() {
        if (supportFragmentManager.findFragmentByTag("CREATE_ALERT_FRAGMENT") != null) return

        val fragment = CreateAlertDialogFragment.newInstance(mCryptoCoinViewModel.cryptoCoin.value!!.symbol)
        fragment.show(supportFragmentManager, "CREATE_ALERT_FRAGMENT")
    }

    private fun saveFavorite() = mCryptoCoinViewModel.saveFavorite()

    private fun setupFavoriteMenuItem(favoriteMenuItem: MenuItem) {
        mCryptoCoinViewModel.isFavorite.observe(this, Observer<Boolean> { isFavorite ->
            if (isFavorite!!) {
                favoriteMenuItem.setIcon(R.drawable.ic_star_white_24dp)
            } else {
                favoriteMenuItem.setIcon(R.drawable.ic_star_border_white_24dp)
            }
        })
    }

    private fun displayPriceHistoryInfo(priceHistory: List<CryptoCoinHistoryPriceItem>?) {
        val dataPoints = priceHistory?.mapTo(ArrayList()) { DataPoint(it.date, it.value) }

        val array = arrayOfNulls<DataPoint>(dataPoints!!.size)
        val series = LineGraphSeries<DataPoint>(dataPoints.toArray(array))

        series.isDrawBackground = true

        val cryptoCoin = mCryptoCoinViewModel.cryptoCoin.value
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
        mCryptoCoinViewModel.displayHistoryPeriod.observe(this, Observer { displayHistoryPeriod ->
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
        title = getString(R.string.cryptocoin_name_format, cryptoCoin?.name, cryptoCoin?.symbol)

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
            formattedPercentage = "+$formattedPercentage"
        }
        tv_percent_change_24h.text = getString(R.string.perc_change_format, formattedPercentage)
    }

    private fun initButtons() {
        bt_one_day.setOnClickListener { updateSelectedPeriod(DAY) }
        bt_one_week.setOnClickListener { updateSelectedPeriod(WEEK) }
        bt_one_month.setOnClickListener { updateSelectedPeriod(MONTH) }
        bt_three_month.setOnClickListener { updateSelectedPeriod(QUARTER_YEAR) }
        bt_six_month.setOnClickListener { updateSelectedPeriod(HALF_YEAR) }
        bt_one_year.setOnClickListener { updateSelectedPeriod(YEAR) }
        bt_all.setOnClickListener { updateSelectedPeriod(ALL) }
    }

    private fun updateSelectedPeriod(newPeriod: Int) {
        mCryptoCoinViewModel.displayHistoryPeriod.value = newPeriod
        updateButtonColors(newPeriod)
    }

    private fun updateButtonColors(selectedPeriod: Int) {
        val colorSelected = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val colorUnselected = ContextCompat.getColor(this, R.color.white)

        bt_one_day.setTextColor(colorUnselected)
        bt_one_month.setTextColor(colorUnselected)
        bt_one_week.setTextColor(colorUnselected)
        bt_three_month.setTextColor(colorUnselected)
        bt_six_month.setTextColor(colorUnselected)
        bt_one_year.setTextColor(colorUnselected)
        bt_all.setTextColor(colorUnselected)

        val selectedButton = when (selectedPeriod) {
            DAY -> bt_one_day
            WEEK -> bt_one_week
            MONTH -> bt_one_month
            QUARTER_YEAR -> bt_three_month
            HALF_YEAR -> bt_six_month
            YEAR -> bt_one_year
            ALL -> bt_one_year
            else -> null
        }
        selectedButton!!.setTextColor(colorSelected)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(STATE_CRYPTO_COIN_ID, mCryptoCoinViewModel.cryptoCoin.value?.symbol!!)
        outState.putInt(STATE_PERIOD, mCryptoCoinViewModel.displayHistoryPeriod.value!!)
    }

    companion object {
        const val STATE_PERIOD = "STATE_PERIOD"
        const val ALPHA = 40
        const val DAY = 1
        const val WEEK = 7
        const val MONTH = 30
        const val QUARTER_YEAR = 90
        const val HALF_YEAR = 180
        const val YEAR = 365
        const val ALL = -1
        const val DEFAULT_PERIOD  = DAY
    }
}

const val EXTRA_CRYPTO_COIN_ID = "CRYPTO_COIN_ID"
const val STATE_CRYPTO_COIN_ID = "CRYPTO_COIN_ID"