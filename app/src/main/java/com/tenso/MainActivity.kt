package com.tenso

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import com.tenso.ui.send.SendActivity
import com.tenso.util.networkUtils
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*


class MainActivity : AppCompatActivity() {

    private var liveData = MutableLiveData<Boolean>()
    private var connected = false
    private var hostWaiting = false
    private var clientWaiting = false
    private var afterConnected = false
    private var timer = Timer()
    private lateinit var scaleDown : Animation
    private var connectionThreads  : Array<Thread?> = Array(2){_->null}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.bindProcessToNetwork(networkUtils.findWlanNetwork(this))
        }

        requestPermission(arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),100)

        val img = findViewById<MaterialButton>(R.id.start)
        scaleDown = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.beacon
        )
        scaleDown.repeatCount = Animation.INFINITE
        img.startAnimation(scaleDown)

        try {
            startConnecting()
        }catch (e : Exception){}

    }

    private fun requestPermission(permission: Array<String>, requestCode: Int) :Boolean{
        permission.filter { ContextCompat.checkSelfPermission(this,it)!= PackageManager.PERMISSION_GRANTED}
        if (permission.isEmpty())return false
        ActivityCompat.requestPermissions(this, permission, requestCode)
        return true
    }
    private fun makeConnection(){
        if (connected)return
        if (hostWaiting)return
        if (connectionThreads[0]==null){
            connectionThreads[0]= Thread{
                val s = ServerSocket()
                s.reuseAddress = true
                try {
                    hostWaiting = true
                    s.bind(InetSocketAddress(networkUtils.getDeviceIpAddress(), networkUtils.getPort()))
                    s.accept()
                    connected=true
                    s.close()
                }catch (e : InterruptedException){
                    hostWaiting = false
                    return@Thread
                }catch (e :Exception){
                    hostWaiting = false
                }
                if (connected){
                    runOnUiThread {
                        liveData.postValue(true)
                    }
                }
            }
        }

        if (Thread.State.RUNNABLE!=connectionThreads[0]?.state){
            hostWaiting = true
            try {
                connectionThreads[0]?.start()
            }catch (e :Exception){}

        }
    }

    private fun joinConnection(){
        fun joinThread():Thread{
            return Thread{
                clientWaiting = true
                while (true){
                    Thread.sleep(1000)
                    val s = Socket()
                    try {
                        s.reuseAddress =true
                        s.bind(InetSocketAddress(networkUtils.getDeviceIpAddress(), s.port))
                        s.connect(InetSocketAddress(networkUtils.getHostIpFromClient(), networkUtils.getPort()),3000)
                        connected = true
                        s.close()
                    }catch (e : InterruptedException){
                        clientWaiting = true
                        return@Thread
                    }catch (e :Exception){
                        clientWaiting = true
                        continue
                    }
                    if (connected){
                        runOnUiThread {
                            liveData.postValue(true)
                        }
                        return@Thread
                    }
                }
            }
        }
        if (connected)return
        if (clientWaiting)return
        if (connectionThreads[1]==null){
            connectionThreads[1]=joinThread()
        }
        try {
            connectionThreads[1]?.start()
        }catch (e :Exception){}

    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    private fun startConnecting(){
        val start = findViewById<MaterialButton>(R.id.start)
        val startButton = findViewById<MaterialButton>(R.id.start_button)
        startButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SendActivity::class.java))
        })
        timer = Timer()
        if (connectionThreads[0]!=null){
            connectionThreads[0]?.interrupt()
        }
        if (connectionThreads[1]!=null){
            connectionThreads[1]?.interrupt()
        }

        liveData = MutableLiveData<Boolean>()
        connected = false
        hostWaiting = false
        clientWaiting = false
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (afterConnected)return
                runOnUiThread {
                    var add = getString(R.string.connecting)
                    if (connected){
                        add = getString(R.string.connected)
                        afterConnected = true
                    }

                    when {
                        networkUtils.isMobileHotspot(this@MainActivity) -> {
                            makeConnection()
                            start.text = getString(R.string.hotspot_on) + add
                            startButton.isEnabled = connected
                        }
                        networkUtils.isWifiOn(this@MainActivity) -> {
                            joinConnection()
                            start.text = getString(R.string.wifi_on) + add
                            startButton.isEnabled = connected
                        }
                        else -> {
                            start.text = getString(R.string.searching_for_network)
                            startButton.isEnabled = false
                        }
                    }
                }
            }
        }, 50, 1000)

        liveData.observe(this, androidx.lifecycle.Observer { result ->
            connected = result
        })
    }
}
