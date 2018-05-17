package com.nightlydev.cryptocointracker.alerts

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinDetailActivity.Companion.DEFAULT_PERIOD
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinViewModel
import com.nightlydev.cryptocointracker.cryptoCoinDetail.EXTRA_CRYPTO_COIN_ID
import com.nightlydev.cryptocointracker.cryptoCoinDetail.STATE_CRYPTO_COIN_ID
import com.nightlydev.cryptocointracker.data.Status
import com.nightlydev.cryptocointracker.data.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_create_alert.*


class CreateAlertDialogFragment : DialogFragment() {
    private lateinit var mCryptoCoinViewModel: CryptoCoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val cryptoCoinId = savedInstanceState?.getString(STATE_CRYPTO_COIN_ID)
                ?: arguments!!.getString(EXTRA_CRYPTO_COIN_ID)

        val viewModelFactory = ViewModelFactory(cryptoCoinId, DEFAULT_PERIOD)
        mCryptoCoinViewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(CryptoCoinViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return activity!!.layoutInflater.inflate(R.layout.fragment_create_alert, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_save -> {
                saveAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (toolbar_top as Toolbar).apply {
            setTitle(R.string.create_alert)
            inflateMenu(R.menu.menu_save)
            setNavigationIcon(R.drawable.ic_close_white_24dp)
            setNavigationOnClickListener { dismiss() }
            setOnMenuItemClickListener { menuItem -> onOptionsItemSelected(menuItem) }
        }

        mCryptoCoinViewModel.cryptoCoin.observe(this, Observer { coin ->
            tv_coin_icon.bindCoin(coin!!)
            tv_coin.text = coin.symbol
            et_price.setText(coin.price.toString())
        })
    }

    private fun saveAlert() {
        mCryptoCoinViewModel.createAlert(
                cryptoCoin = mCryptoCoinViewModel.cryptoCoin.value!!,
                price = et_price.text.toString().toDouble(),
                note = et_note.text.toString(),
                persistent = switch_persistent.isChecked
        ).observe(this, Observer { result ->
            progress_bar.visibility = GONE
            when(result?.status) {
                Status.LOADING -> progress_bar.visibility = VISIBLE
                Status.SUCCESS -> dismiss()
                Status.ERROR -> {}
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(STATE_CRYPTO_COIN_ID, mCryptoCoinViewModel.cryptoCoin.value?.symbol!!)
    }

    companion object {
        fun newInstance(cryptoCoinId: String): CreateAlertDialogFragment {
            return CreateAlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_CRYPTO_COIN_ID, cryptoCoinId)
                }
            }
        }
    }
}