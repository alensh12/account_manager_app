package com.example.acc_manager_test

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var mAccountManager: AccountManager? = null
    private var mAlertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAccountManager = AccountManager.get(this);
        findViewById<Button>(R.id.login_button).setOnClickListener(onLoginClickLister);
    }

    private val onLoginClickLister: View.OnClickListener = View.OnClickListener {
        val availableAccounts = mAccountManager!!.getAccountsByType("np.com.worldlink.worldlinkapp")
        if (availableAccounts.size == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_LONG).show();
            mAccountManager?.addAccount(
                "np.com.worldlink.worldlinkapp",
                "Full access",
                null,
                null,
                this,
                AccountManagerCallback {
                    Log.e("MANAGER APP", "CALLED BACK");
                },
                null
            )
//            mAccountManager?.addAccount()
        } else {
            val name = arrayOfNulls<String>(availableAccounts.size)
            for (i in availableAccounts.indices) {
                name[i] = availableAccounts[i].name
            }
            mAlertDialog = AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(
                ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, name
                )
            ) { dialog, which ->
                getExistingAccountAuthToken(
                    availableAccounts[which],
                    "np.com.worldlink.worldlinkapp"
                );

            }.create()
            mAlertDialog!!.show();
        }

    }

    private fun getExistingAccountAuthToken(account: Account?, authTokenType: String) {
        val userData = mAccountManager?.getUserData(account, "token");
        Log.e("myWorldlink", "USER DATA $userData");
//        val future = mAccountManager!!.getAuthToken(account, authTokenType, null, this, null, null)
//
//        Thread {
//            try {
//                val bnd = future.result
//                val authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN)
////                showMessage(if (authtoken != null) "SUCCESS!\ntoken: $authtoken" else "FAIL")
//                Log.d("myWorldlink", "GetToken Bundle is $bnd $authtoken")
//            } catch (e: Exception) {
//                e.printStackTrace()
////                showMessage(e.message)
//            }
//        }.start()
    }
}