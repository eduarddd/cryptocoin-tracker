package com.nightlydev.cryptocointracker.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.database.FirebaseDatabase



/**
 * Created by edu on 1-2-18.
 */
class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken)

        sendTokenToServer(refreshedToken)
    }

    private fun sendTokenToServer(token: String?) {
        val dbRef = FirebaseDatabase.getInstance().getReference()

        dbRef.child("users").child(getUserId())
                .child("notificationTokens").setValue(token)
    }

    private fun getUserId() : String {
        return "edu"
    }

    companion object {
        val TAG = "InstanceIdService"
    }
}