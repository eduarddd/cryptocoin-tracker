package com.nightlydev.cryptocointracker

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.nightlydev.cryptocointracker.cryptoCoinOverview.OverviewActivity


/**
 * @author edu (edusevilla90@gmail.com)
 * @since 3-5-18
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFirebaseAuth = FirebaseAuth.getInstance()
        val user = mFirebaseAuth.currentUser

        if (user != null) {
            startOverviewActivity()
            return
        }

        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInAnonymously:failure", task.exception)
                    }

                    startOverviewActivity()
                })
    }

    private fun startOverviewActivity() {
        val intent = Intent(this, OverviewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}