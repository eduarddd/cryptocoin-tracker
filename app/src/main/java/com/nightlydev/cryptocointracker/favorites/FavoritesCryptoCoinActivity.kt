package com.nightlydev.cryptocointracker.favorites

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinDetailActivity
import com.nightlydev.cryptocointracker.cryptoCoinOverview.CryptoCoinsAdapter
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.ui.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.toolbar_top.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 16-1-18
 */
class FavoritesCryptoCoinActivity : AppCompatActivity(),
        SwipeRefreshLayout.OnRefreshListener,
        CryptoCoinsAdapter.OnClickHandler {

    private var mAdapter = CryptoCoinsAdapter(this@FavoritesCryptoCoinActivity)
    private var mFavoritesViewModel : FavoritesViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initRecyclerView()
        initViewModel()
    }

    private fun initViewModel() {
        mFavoritesViewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
        mFavoritesViewModel?.getFavoriteCoinsList()?.observe(
                this,
                Observer { favoriteCoinsList ->
                    swipe_refresh_layout.isRefreshing = false
                    if (favoriteCoinsList != null) {
                        mAdapter.setItems(favoriteCoinsList)
                    }
                }
        )

    }

    private fun initRecyclerView() {
        with(swipe_refresh_layout) {
            setOnRefreshListener(this@FavoritesCryptoCoinActivity)
        }
        with(rv_crypto_coins_overview) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@FavoritesCryptoCoinActivity)
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(this@FavoritesCryptoCoinActivity))
        }
    }

    override fun onRefresh() {
        mFavoritesViewModel?.refreshCryptoCoinList()
    }

    override fun onClick(cryptoCoin: CryptoCoin) {
        startCryptoCoinDetailActivity(cryptoCoin)
    }

    private fun startCryptoCoinDetailActivity(cryptoCoin: CryptoCoin) {
        val intent = Intent(this, CryptoCoinDetailActivity::class.java)
        intent.putExtra(CryptoCoinDetailActivity.EXTRA_CRYPTO_COIN_ID, cryptoCoin.shortName)
        startActivity(intent)
    }
}