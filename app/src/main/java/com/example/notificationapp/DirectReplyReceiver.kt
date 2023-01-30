package com.example.notificationapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.RemoteInput


class DirectReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val remoteInput: Bundle? = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null && context != null) {
                val replyText = remoteInput.getCharSequence("key_text_reply")
                val answer = Message(replyText, null)
                MainActivity.MESSAGES.add(answer)
                MainActivity.sendChannel1Notification(context)
            }
        }
    }
}