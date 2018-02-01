package com.nightlydev.cryptocointracker.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by edu on 1-2-18.
 */
class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken)

        saveToken(refreshedToken)
    }

    private fun saveToken(token: String?) {

    }

    companion object {
        val TAG = "InstanceIdService"
    }
}