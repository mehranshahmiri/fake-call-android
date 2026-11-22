package com.fakecall.app

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.telecom.PhoneAccountHandle

object Caller {
    const val NAME = "Mom"
    const val NUMBER = "5550100"
    const val DELAY_MS = 60_000L

    val address: Uri = Uri.fromParts("tel", NUMBER, null)
    fun handle(c: Context) =
        PhoneAccountHandle(ComponentName(c, FakeConnectionService::class.java), "fakecall")
}
