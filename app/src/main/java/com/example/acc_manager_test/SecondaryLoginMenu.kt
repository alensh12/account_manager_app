package com.example.acc_manager_test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SecondaryLoginMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary_login_menu)

        findViewById<Button>(R.id.btn_sms_login).setOnClickListener {
            startActivity(Intent(this, OTPActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btn_qr_login).setOnClickListener {
            startActivity(Intent(this, BarCodeScannerActivity::class.java))
            finish()
        }
    }
}