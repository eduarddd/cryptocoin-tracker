package com.nightlydev.cryptocointracker.fcm

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


/**
 * Created by edu on 1-2-18.
 */

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: $refreshedToken")

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                sendTokenToServer(refreshedToken)
            }
        }
    }

    private fun sendTokenToServer(token: String?) {
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("users").child(getUserId())
                .child("notificationToken").setValue(token)
    }

    private fun getUserId() : String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    companion object {
        const val TAG = "InstanceIdService"
    }
}
