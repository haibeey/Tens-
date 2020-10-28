package com.sender.ui.send

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.sender.R
import com.sender.client.Client
import com.sender.host.Host
import com.sender.models.TransferFile
import com.sender.util.networkUtils
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class SendActivity : AppCompatActivity() {

    private val thingsToSend = mutableSetOf<TransferFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 7
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)


        var updated = true
        var host = true

        mkdir()

        when {
            networkUtils.isMobileHotspot(this) -> { }
            networkUtils.isWifiOn(this) -> {
                host = false
            }
            else -> {
                updated = false
                val snackBar = Snackbar.make(viewPager,R.string.not_connected,Snackbar.LENGTH_INDEFINITE)
                snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                snackBar.show()
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            exit()
                        }
                    }
                },2000)
            }
        }

        if (updated){
            findViewById<MaterialButton>(R.id.sentItems).setOnClickListener {
                if (thingsToSend.size<=0)return@setOnClickListener
                if (host){
                    handleHost()
                }else{
                   handleClient()
                }
            }
        }
    }


    private fun handleHost(){

        Thread{
            val host1 = Host(itemsToSend = toArrayList(thingsToSend),
                context = this,
                ip=networkUtils.getDeviceIpAddress(),
                port = networkUtils.getPort()
            )
            host1.send()
        }.start()

        Thread{
            val host2 = Host(itemsToSend = toArrayList(thingsToSend),
                context = this,
                ip=networkUtils.getDeviceIpAddress(),
                port = networkUtils.getPort()+1
            )
            host2.receive()
        }.start()


    }

    private fun handleClient(){

        Thread{
            val client1 = Client(itemsToSend = toArrayList(thingsToSend),
                context = this,
                ip=networkUtils.getHostIpFromClient(),
                port = networkUtils.getPort()
            )
            client1.send()
        }.start()

        Thread{
            val client2 = Client(itemsToSend = toArrayList(thingsToSend),
                context = this,
                ip=networkUtils.getHostIpFromClient(),
                port = networkUtils.getPort()+1
            )
            client2.receive()
        }.start()
    }

    fun addToThingsToSend(item : TransferFile){
        thingsToSend.add(item)
    }

    fun removeFromThingsToSend(item : TransferFile){
        if (thingsToSend.contains(item)){
            thingsToSend.remove(item)
        }

    }
    fun sizeOfThingsToSend():Int{
        return thingsToSend.size
    }

    private fun exit(){
        exitProcess(0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exit()
    }

    private fun toArrayList(items : Set<TransferFile>):ArrayList<TransferFile>{
        val result =ArrayList<TransferFile>()
        items.forEach {
            result.add(it)
        }
        return  result
    }


    private fun mkdir(){
        val f = File("/sdcard/Tensõ/")
        if (!f.exists()){
            f.mkdir()
            arrayOf("App","Videos","Doc","Audio","Images").forEach {
                val subFolder = File("${f.absolutePath}/${it}/")
                subFolder.mkdir()
            }
        }
    }
}