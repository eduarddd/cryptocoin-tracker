package com.nightlydev.cryptocointracker

import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_overview.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class OverviewActivity : Activity(), SwipeRefreshLayout.OnRefreshListener {
    private var mAdapter = CryptoCoinsAdapter()

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

    private fun initRecyclerView() {
        with(swipe_refresh_layout) {
            setOnRefreshListener(this@OverviewActivity)
        }
        with(rv_crypto_coins_overview) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@OverviewActivity)
            mAdapter = CryptoCoinsAdapter()
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(this@OverviewActivity))
        }
    }

    private fun fetchCryptoCoinsList() {
        val repository = CryptoCoinRepository(CryptoCoinService.create())
        repository.listCryptoCoins()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result -> mAdapter.setItems(result)
                    swipe_refresh_layout.isRefreshing = false
                }, {
                    error -> error.printStackTrace()
                    swipe_refresh_layout.isRefreshing = false
                })
    }
}
