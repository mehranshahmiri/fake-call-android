package com.fakecall.app

import android.net.Uri
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager

/** Hands Android's own dialer an incoming call, so the system shows the real call screen. */
class FakeConnectionService : ConnectionService() {
    override fun onCreateIncomingConnection(
        account: PhoneAccountHandle?, request: ConnectionRequest?
    ): Connection = object : Connection() {
        override fun onAnswer() { setActive() }
        override fun onReject() { setDisconnected(DisconnectCause(DisconnectCause.REJECTED)); destroy() }
        override fun onDisconnect() { setDisconnected(DisconnectCause(DisconnectCause.LOCAL)); destroy() }
        override fun onAbort() { setDisconnected(DisconnectCause(DisconnectCause.CANCELED)); destroy() }
    }.apply {
        setAddress(Caller.address, TelecomManager.PRESENTATION_ALLOWED)
        setCallerDisplayName(Caller.NAME, TelecomManager.PRESENTATION_ALLOWED)
        connectionCapabilities = Connection.CAPABILITY_MUTE
        setInitializing()
        setRinging()
    }
}
