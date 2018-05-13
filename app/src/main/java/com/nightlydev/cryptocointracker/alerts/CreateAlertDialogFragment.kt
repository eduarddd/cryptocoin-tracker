package com.nightlydev.cryptocointracker.alerts

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinDetailActivity.Companion.DEFAULT_PERIOD
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinViewModel
import com.nightlydev.cryptocointracker.cryptoCoinDetail.EXTRA_CRYPTO_COIN_ID
import com.nightlydev.cryptocointracker.cryptoCoinDetail.STATE_CRYPTO_COIN_ID
import com.nightlydev.cryptocointracker.data.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_create_alert.view.*

class CreateAlertDialogFragment : DialogFragment() {
    private lateinit var mView: View
    private lateinit var mCryptoCoinViewModel: CryptoCoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cryptoCoinId = savedInstanceState?.getString(STATE_CRYPTO_COIN_ID)
                ?: arguments!!.getString(EXTRA_CRYPTO_COIN_ID)

        val viewModelFactory = ViewModelFactory(cryptoCoinId, DEFAULT_PERIOD)
        mCryptoCoinViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(CryptoCoinViewModel::class.java)
        mCryptoCoinViewModel.cryptoCoin.observe(this, Observer { coin ->
            mView.tv_coin.text = coin?.symbol
            mView.et_price.setText(coin?.price.toString())
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = activity!!.layoutInflater.inflate(R.layout.fragment_create_alert, null)

        val dialogBuilder = AlertDialog.Builder(context!!).apply {
            setView(mView)
            setPositiveButton(R.string.save) { _, _ -> saveAlert() }
            setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
        }

        return dialogBuilder.create()
    }

    private fun saveAlert() {
        mCryptoCoinViewModel.createAlert(
                cryptoCoin = mCryptoCoinViewModel.cryptoCoin.value!!,
                price = mView.et_price.text.toString().toDouble(),
                note = mView.et_note.text.toString(),
                persistent = mView.switch_persistent.isChecked)
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