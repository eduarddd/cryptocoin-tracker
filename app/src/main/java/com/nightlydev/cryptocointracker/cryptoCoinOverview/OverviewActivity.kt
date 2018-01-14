package com.nightlydev.cryptocointracker.cryptoCoinOverview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinDetailActivity
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.ui.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.toolbar_top.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class OverviewActivity : AppCompatActivity(),
        SwipeRefreshLayout.OnRefreshListener,
        CryptoCoinsAdapter.OnClickHandler {


    private lateinit var mAdapter : CryptoCoinsAdapter
    private var mOverviewViewModel : OverviewViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        setSupportActionBar(toolbar)
        initRecyclerView()
        mOverviewViewModel = ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        subscribeToCryptoCoinList()
        subscribeToSearchQuery()

        if (savedInstanceState != null) {
            val searchQuery = savedInstanceState.getString(STATE_SEARCH_QUERY)
            mOverviewViewModel?.getSearchQuery()?.value = searchQuery
        }
    }

    private fun subscribeToCryptoCoinList() {
        mOverviewViewModel?.getCryptoCoinList()?.observe(
                this,
                Observer { cryptoCoinList ->
                    swipe_refresh_layout.isRefreshing = false
                    if (cryptoCoinList != null) {
                        mAdapter.setItems(cryptoCoinList)
                    }
                })
    }

    private fun subscribeToSearchQuery() {
        mOverviewViewModel?.getSearchQuery()?.observe(
                this,
                Observer { searchQuery -> mAdapter.filter(searchQuery) }
        )
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

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState?.putString(STATE_SEARCH_QUERY, mOverviewViewModel?.getSearchQuery()?.value)
    }

    override fun onRefresh() {
        mOverviewViewModel?.refreshCryptoCoinList()
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
                mOverviewViewModel?.getSearchQuery()?.value = searchQuery
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
    }


    private fun startCryptoCoinDetailActivity(cryptoCoin: CryptoCoin) {
        val intent = Intent(this, CryptoCoinDetailActivity::class.java)
        intent.putExtra(CryptoCoinDetailActivity.EXTRA_CRYPTO_COIN_ID, cryptoCoin.id)
        startActivity(intent)
    }

    companion object {
        val STATE_SEARCH_QUERY = "SEARCH_QUERY"
    }
}
