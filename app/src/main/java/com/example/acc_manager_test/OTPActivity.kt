package com.example.acc_manager_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mukesh.OtpView

class OTPActivity : AppCompatActivity() {
    private var otpView: OtpView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        otpView = findViewById(R.id.otp_view)
        otpView?.setOtpCompletionListener {  otp ->
            Toast.makeText(this, otp, Toast.LENGTH_LONG).show()
        }
    }
}