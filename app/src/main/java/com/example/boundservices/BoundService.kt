package com.example.boundservices

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class BoundService : Service() {

    private val binder = LocalBinder()

    fun getResult(): String {
        println("BoundService: getResult")
        return "This is the result from the bound service"
    }

    inner class LocalBinder : Binder() {
        fun getService(): BoundService = this@BoundService
    }

    override fun onBind(p0: Intent?): IBinder {
        println("BoundService: onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("BoundService: onUnbind")
        return super.onUnbind(intent)
    }

}