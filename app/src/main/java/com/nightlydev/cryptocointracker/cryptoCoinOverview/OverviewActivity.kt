package com.nightlydev.cryptocointracker.cryptoCoinOverview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinDetailActivity
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinDetailActivity.Companion.EXTRA_CRYPTO_COIN_ID
import com.nightlydev.cryptocointracker.data.Status
import com.nightlydev.cryptocointracker.favorites.FavoritesCryptoCoinActivity
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.ui.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.toolbar_top.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class OverviewActivity : AppCompatActivity() {

    private lateinit var mAdapter : CryptoCoinsAdapter
    private lateinit var mOverviewViewModel : OverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        setSupportActionBar(toolbar)
        initRecyclerView()
        initViewModel(savedInstanceState)
    }

    private fun initViewModel(savedInstanceState: Bundle?) {
        mOverviewViewModel = ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        if (savedInstanceState != null) {
            val searchQuery = savedInstanceState.getString(STATE_SEARCH_QUERY)
            mOverviewViewModel.searchQuery.value = searchQuery
        }

        subscribeToCryptoCoinList()
        subscribeToSearchQuery()
    }

    private fun subscribeToCryptoCoinList() {
        mOverviewViewModel.cryptoCoinList.observe(this, Observer { cryptoCoinList ->
            swipe_refresh_layout.isRefreshing = false
            when (cryptoCoinList?.status) {
                Status.LOADING ->  swipe_refresh_layout.isRefreshing = true
                Status.SUCCESS ->
                    if (cryptoCoinList.data != null) {
                        mAdapter.cryptoCoins = ArrayList(cryptoCoinList.data!!)
                    }
                Status.ERROR -> {} //TODO: Handle error
            }
        })
    }

    private fun subscribeToSearchQuery() {
        mOverviewViewModel.searchQuery.observe(this, Observer { searchQuery ->
            mAdapter.filter.filter(searchQuery) }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_overview, menu)
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        initSearchView(searchView)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_search -> true
            R.id.action_favorites -> {
                startFavoritesCryptoCoinActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState?.putString(STATE_SEARCH_QUERY, mOverviewViewModel.searchQuery.value)
    }

    private fun initSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                mOverviewViewModel.searchQuery.value = searchQuery
                return true
            }
        })
    }

    private fun initRecyclerView() {
        swipe_refresh_layout.apply {
            setOnRefreshListener { mOverviewViewModel.refreshCryptoCoinList() }
        }
        mAdapter = CryptoCoinsAdapter()
        mAdapter.clickHandler = {cryptoCoin -> startCryptoCoinDetailActivity(cryptoCoin) }
        rv_crypto_coins_overview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@OverviewActivity)
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(this@OverviewActivity))
        }
    }

    private fun startCryptoCoinDetailActivity(cryptoCoin: CryptoCoin) {
        val intent = Intent(this, CryptoCoinDetailActivity::class.java).apply {
            putExtra(EXTRA_CRYPTO_COIN_ID, cryptoCoin.shortName)
        }
        startActivity(intent)
    }

    private fun startFavoritesCryptoCoinActivity() {
        val intent = Intent(this, FavoritesCryptoCoinActivity::class.java)
        startActivity(intent)
    }

    companion object {
        const val STATE_SEARCH_QUERY = "SEARCH_QUERY"
    }
}
