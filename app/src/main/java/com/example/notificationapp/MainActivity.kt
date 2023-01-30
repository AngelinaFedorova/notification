package com.example.notificationapp

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.notificationapp.App.Companion.CHANNEL_1_ID
import com.example.notificationapp.App.Companion.CHANNEL_2_ID

class MainActivity : AppCompatActivity() {
    private var notificationManager: NotificationManagerCompat? = null
    private var editTextTitle: EditText? = null
    private var editTextMessage: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManager = NotificationManagerCompat.from(this)
        editTextTitle = findViewById(R.id.edit_text_title)
        editTextMessage = findViewById(R.id.edit_text_message)
        MESSAGES.add(Message("Good morning!", null))
        MESSAGES.add(Message("Hello", null))
        MESSAGES.add(Message("Hi!", null))
    }

    fun sendOnChannel1(v: View?) {
        sendChannel1Notification(this)
    }

    fun sendOnChannel2(v: View?) {
        val title1 = "Title 1"
        val message1 = "Message 1 ${System.currentTimeMillis()}"
        val title2 = "Title 2"
        val message2 = "Message 2"
        val notification1: Notification = NotificationCompat.Builder(this, CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_two)
            .setContentTitle(title1)
            .setContentText(message1)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setGroup("example_group")
            .build()
        val notification2: Notification = NotificationCompat.Builder(this, CHANNEL_2_ID)
            .setSmallIcon(R.drawable.ic_two)
            .setContentTitle(title2)
            .setContentText(message2)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup("example_group")
            .build()
        val summaryNotification: Notification = NotificationCompat.Builder(this, CHANNEL_2_ID)
            .setSmallIcon(R.drawable.ic_reply)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("$title2 $message2")
                    .addLine("$title1 $message1")
                    .setBigContentTitle("2 new messages")
                    .setSummaryText("user@example.com")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup("example_group")
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setGroupSummary(true)
            .build()
        notificationManager?.notify(2, notification1)
        SystemClock.sleep(10000)
        sendOnChannel2(v)
//        notificationManager?.notify(3, notification2)
//        notificationManager?.notify(4, summaryNotification)
    }

    companion object {
        var MESSAGES: MutableList<Message> = ArrayList()
        fun sendChannel1Notification(context: Context) {
            val activityIntent = Intent(context, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(
                context,
                0, activityIntent, PendingIntent.FLAG_MUTABLE
            )
            val remoteInput: RemoteInput = RemoteInput.Builder("key_text_reply")
                .setLabel("Your answer...")
                .build()
            val replyIntent: Intent
            var replyPendingIntent: PendingIntent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                replyIntent = Intent(context, DirectReplyReceiver::class.java)
                replyPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0, replyIntent,  PendingIntent.FLAG_MUTABLE
                )
            } else {
                //start chat activity instead (PendingIntent.getActivity)
                //cancel notification with notificationManagerCompat.cancel(id)
            }
            val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
                R.drawable.ic_reply,
                "Reply",
                replyPendingIntent
            ).addRemoteInput(remoteInput).build()
            val messagingStyle = NotificationCompat.MessagingStyle("Me")
            messagingStyle.conversationTitle = "Group Chat"
            for (chatMessage in MESSAGES) {
                val notificationMessage: NotificationCompat.MessagingStyle.Message =
                    NotificationCompat.MessagingStyle.Message(
                        chatMessage.text,
                        chatMessage.timestamp,
                        chatMessage.sender
                    )
                messagingStyle.addMessage(notificationMessage)
            }
            val notification: Notification = NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setStyle(messagingStyle)
                .addAction(replyAction)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build()
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1, notification)
            MESSAGES.add(Message("${System.currentTimeMillis()}", null ))
            SystemClock.sleep(2000)
            sendChannel1Notification(context)
        }
    }
}