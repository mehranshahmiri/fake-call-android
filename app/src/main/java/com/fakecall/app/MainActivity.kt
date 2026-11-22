package com.fakecall.app

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.telecom.PhoneAccount
import android.telecom.TelecomManager
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var fireAt = 0L

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        status = TextView(this).apply { textSize = 22f; setTextColor(Color.WHITE); gravity = Gravity.CENTER }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#14161B"))
            setPadding(48, 48, 48, 48)
            addView(TextView(context).apply {
                text = "${Caller.NAME}\n${Caller.NUMBER}"
                textSize = 16f; setTextColor(Color.parseColor("#A0A6B0")); gravity = Gravity.CENTER
            })
            addView(status)
            addView(Button(context).apply { text = "Schedule again"; setOnClickListener { schedule() } })
            // One-time step if calls don't arrive: switch on "Fake Call" under calling accounts.
            addView(Button(context).apply {
                text = "Enable calling account"
                setOnClickListener { startActivity(Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS)) }
            })
        })
        registerAccount()
        if (b == null) schedule()
    }

    private fun registerAccount() {
        val tm = getSystemService(TelecomManager::class.java)
        tm.registerPhoneAccount(
            PhoneAccount.builder(Caller.handle(this), "Mobile")
                .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                .addSupportedUriScheme(PhoneAccount.SCHEME_TEL)
                .build()
        )
    }

    private fun schedule() {
        val pi = PendingIntent.getBroadcast(
            this, 0, Intent(this, CallReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val at = System.currentTimeMillis() + Caller.DELAY_MS
        getSystemService(AlarmManager::class.java).setAlarmClock(AlarmManager.AlarmClockInfo(at, pi), pi)
        fireAt = SystemClock.elapsedRealtime() + Caller.DELAY_MS
        handler.removeCallbacksAndMessages(null)
        handler.post(object : Runnable {
            override fun run() {
                val left = ((fireAt - SystemClock.elapsedRealtime()) / 1000).toInt()
                status.text = if (left > 0) "Calling in ${left}s" else "Call sent"
                if (left > 0) handler.postDelayed(this, 500)
            }
        })
    }
}
