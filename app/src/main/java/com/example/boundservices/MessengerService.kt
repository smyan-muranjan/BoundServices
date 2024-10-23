package com.example.boundservices

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.widget.Toast

class MessengerService : Service() {

    private lateinit var messenger: Messenger

    internal class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            println("MessengerService: handleMessage")
            when (msg.what) {
                SAY_HELLO -> {
                    Toast.makeText(applicationContext, "Hello", Toast.LENGTH_LONG).show()
                }
                SIMULATE_LONG_OPERATION -> {

                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        println("MessengerService: onBind")
        messenger = Messenger(IncomingHandler(this))
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("MessengerService: onUnbind")
        return super.onUnbind(intent)
    }

    companion object Actions {
        const val SAY_HELLO = 1
        const val SIMULATE_LONG_OPERATION = 2
    }

}