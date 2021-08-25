package com.example.acc_manager_test

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private var mAccountManager: AccountManager? = null
    private var mAlertDialog: AlertDialog? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                System.out.println(data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                val accountType = data?.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                val accountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                try {
                    val future = mAccountManager!!.getAuthToken(
                        Account(accountName, accountType),
                        "Full access",
                        null,
                        this,
                        null,
                        null
                    )
                    Thread {
                        try {
                            val bnd = future.result
                            val authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN)
                            Log.d("myWorldLink", "GetToken Bundle is $bnd $authtoken")
                            this.runOnUiThread {
                                Toast.makeText(this, "Token $authtoken", Toast.LENGTH_LONG).show();
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                } catch (e: Exception) {
                    println(e.printStackTrace());
                }


            }

        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    getAccounts();
                }
            }
        mAccountManager = AccountManager.get(this);
        findViewById<Button>(R.id.login_button).setOnClickListener(onLoginClickLister);
    }

    private val onLoginClickLister: View.OnClickListener = View.OnClickListener {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestAccountManagerPermission()
            } else {
                getAccounts();
            }

        } catch (e: Exception) {
            print(e);
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestAccountManagerPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // The permission is granted
                // you can go with the flow that requires permission here
                getAccounts();
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS) -> {
                // This case means user previously denied the permission
                // So here we can display an explanation to the user
                // That why exactly we need this permission
                showPermissionRequestExplanation(
                    "READ CONTACT",
                    "REQUEST"
                ) { requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS) }
            }
            else -> {
                // Everything is fine you can simply request the permission
                requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getAccounts() {
        val packagename = "np.com.worldlink.worldlinkapp"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isAppInstalled(packagename)) {
                val accountChooserIntent = AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    arrayOf(packagename),
                    null,
                    null,
                    null,
                    null
                )
                resultLauncher.launch(accountChooserIntent)
            } else {
                Toast.makeText(this, "App not installed", Toast.LENGTH_LONG).show();
            }

        }
//        val availableAccounts =
//            mAccountManager!!.getAccountsByType(packagename)
//        if (availableAccounts.isEmpty()) {
//            if (isAppInstalled(packagename)) {
//                mAccountManager?.addAccount(
//                    packagename,
//                    "Full access",
//                    null,
//                    null,
//                    this,
//                    AccountManagerCallback {
//                        Log.e("MANAGER APP", "CALLED BACK");
//                        val bnd: Bundle = it.result
//                        Log.e("MANAGER APP", "ACCOUNT CREATED" + bnd);
//                    },
//                    null
//                )
//            } else {
//                Toast.makeText(this, "App not installed", Toast.LENGTH_LONG).show();
//            }
//            //            mAccountManager?.addAccount()
//        } else {
//            val name = arrayOfNulls<String>(availableAccounts.size)
//            for (i in availableAccounts.indices) {
//                name[i] = availableAccounts[i].name
//            }
//            mAlertDialog = AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(
//                ArrayAdapter<String>(
//                    this, android.R.layout.simple_list_item_1, name
//                )
//            ) { dialog, which ->
//                getExistingAccountAuthToken(
//                    availableAccounts[which],
//                    packagename
//                );
//
//            }.create()
//            mAlertDialog!!.show();
//        }
    }

    private fun isAppInstalled(packagename: String): Boolean {
        val pm: PackageManager = packageManager
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {

        }
        return false
    }

    private fun getExistingAccountAuthToken(account: Account?, authTokenType: String) {

        account.let {
            val token = mAccountManager?.getUserData(it, "token");
            val accountType = mAccountManager?.getUserData(it, "account_type");
            Log.e("myWorldlink", "USER DATA $token $accountType");
        }
    }

    fun Context.showPermissionRequestExplanation(
        permission: String,
        message: String,
        retry: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(this).apply {
            setTitle("$permission Required")
            setMessage(message)
            setPositiveButton("Ok") { _, _ -> retry?.invoke() }
        }.show()
    }


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
