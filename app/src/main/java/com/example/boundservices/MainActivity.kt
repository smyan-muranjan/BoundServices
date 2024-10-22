package com.example.boundservices

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.boundservices.ui.theme.BoundServicesTheme

class MainActivity : ComponentActivity() {

    private var boundService: BoundService? = null
    private var boundBound: Boolean = false

    private val boundConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            println("boundConnection: onServiceConnected")
            val binder = service as BoundService.LocalBinder
            this@MainActivity.boundService = binder.getService()
            boundBound = true
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            println("boundConnection: onServiceDisconnected")
            this@MainActivity.boundService = null
            boundBound = false
        }
    }

    private var messengerService: Messenger? = null
    private var messengerBound: Boolean = false

    private val messengerConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            println("messengerConnection: onServiceConnected")
            messengerService = Messenger(service)
            messengerBound = true
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            println("messengerConnection: onServiceDisconnected")
            messengerService = null
            messengerBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoundServicesTheme {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                if (boundBound) {
                                    Toast.makeText(this@MainActivity, boundService?.getResult(), Toast.LENGTH_LONG).show()
                                }
                            }
                        ) {
                            Text(text="Make Bound Service Call")
                        }

                        Button(
                            onClick = {
                                if (messengerBound) {
                                    val message = Message.obtain(null, MessengerService.SAY_HELLO, 0, 0)
                                    try {
                                        messengerService?.send(message)
                                    } catch (e: RemoteException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        ) {
                            Text(text="Send Hello Message")
                        }

                        Button(
                            onClick = {
                                if (messengerBound) {
                                    val message = Message.obtain(null, MessengerService.SIMULATE_LONG_OPERATION, 0, 0)
                                    try {
                                        messengerService?.send(message)
                                    } catch (e: RemoteException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        ) {
                            Text(text="Send Long Operation message")
                        }
                    }
            }
        }
    }

    override fun onStart() {
        println("onStart")
        super.onStart()
        println("Sending Bound Service Intent")
        Intent(this, BoundService::class.java).also {
            bindService(it, boundConnection, Context.BIND_AUTO_CREATE)
        }

        println("Sending Messenger Service Intent")
        Intent(this, MessengerService::class.java).also {
            bindService(it, messengerConnection, Context.BIND_AUTO_CREATE)
        }

    }

    override fun onStop() {
        println("onStop")
        super.onStop()
        unbindService(boundConnection)
        boundBound = false

        if (messengerBound) {
            unbindService(messengerConnection)
            messengerBound = false
        }
    }
}