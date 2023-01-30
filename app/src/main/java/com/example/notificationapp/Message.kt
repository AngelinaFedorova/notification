package com.example.notificationapp

class Message(val text: CharSequence?, val sender: CharSequence?) {
    val timestamp: Long

    init {
        timestamp = System.currentTimeMillis()
    }
}