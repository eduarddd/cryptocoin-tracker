package com.nightlydev.cryptocointracker.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import com.nightlydev.cryptocointracker.App
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_overview.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class OverviewActivity : Activity(), SwipeRefreshLayout.OnRefreshListener, CryptoCoinsAdapter.OnClickHandler {

    private lateinit var mAdapter : CryptoCoinsAdapter
    val cryptoCoinRepository = CryptoCoinRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        fetchCryptoCoinsList()
    }

    override fun onRefresh() {
        fetchCryptoCoinsList()
    }

    override fun onClick(cryptoCoin: CryptoCoin) {
        startCryptoCoinDetailActivity(cryptoCoin)
    }

    private fun initRecyclerView() {
        with(swipe_refresh_layout) {
            setOnRefreshListener(this@OverviewActivity)
        }
        with(rv_crypto_coins_overview) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@OverviewActivity)
            mAdapter = CryptoCoinsAdapter(this@OverviewActivity)
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(this@OverviewActivity))
        }
    }

    private fun fetchCryptoCoinsList() {
        cryptoCoinRepository.listCryptoCoins()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result ->
                    saveCryptoCoins(result)
                    swipe_refresh_layout.isRefreshing = false
                }, {
                    error ->
                    //error.printStackTrace()
                    swipe_refresh_layout.isRefreshing = false
                })
    }

    private fun saveCryptoCoins(cryptoCoinList: List<CryptoCoin>) {
        Single.fromCallable {
            App.cryptoCoinDatabase?.cryptoCoinDao()?.insertAll(cryptoCoinList)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun startCryptoCoinDetailActivity(cryptoCoin: CryptoCoin) {
        val intent = Intent(this, CryptoCoinDetailActivity::class.java)
        intent.putExtra(CryptoCoinDetailActivity.EXTRA_CRYPTO_COIN, cryptoCoin)
        startActivity(intent)
    }
}
