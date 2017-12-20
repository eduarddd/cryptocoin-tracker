package com.nightlydev.cryptocointracker.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
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
class OverviewActivity : Activity(),
        SwipeRefreshLayout.OnRefreshListener,
        CryptoCoinsAdapter.OnClickHandler {

    private lateinit var mAdapter : CryptoCoinsAdapter
    private val cryptoCoinRepository = CryptoCoinRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_overview, menu)
        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        initSearchView(searchView)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_search) {
            return true
        }

        return super.onOptionsItemSelected(item)
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

    private fun initSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                mAdapter.filter(searchQuery)
                rv_crypto_coins_overview.scrollToPosition(0)
                return true
            }
        })
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
        App.cryptoCoinDatabase?.cryptoCoinDao()?.getAllCryptoCoins()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { cryptoCoinList ->
                    mAdapter.setItems(cryptoCoinList)
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
                    error.printStackTrace()
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
