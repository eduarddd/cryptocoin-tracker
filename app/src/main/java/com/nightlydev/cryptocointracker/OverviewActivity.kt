package com.nightlydev.cryptocointracker

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_overview.*

/**
 * Created by eduardo on 12/5/17.
 */
class OverviewActivity : Activity() {

    private var mAdapter = CryptoCoinsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        initRecyclerView()
        createFakeData()
    }

    private fun initRecyclerView() {
        with(rv_crypto_coins_overview) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@OverviewActivity)
            mAdapter = CryptoCoinsAdapter()
            adapter = mAdapter
        }
    }


    private fun createFakeData() {
        for(i in 1..10) {
            val fake = CryptoCoin("1", "Bitcoin", "BTC", 1, 11971.5)
            mAdapter.add(fake)
        }
    }
}
