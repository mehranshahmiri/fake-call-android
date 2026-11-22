package com.fakecall.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        val tm = c.getSystemService(TelecomManager::class.java)
        tm.addNewIncomingCall(
            Caller.handle(c),
            Bundle().apply { putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, Caller.address) }
        )
    }
}
