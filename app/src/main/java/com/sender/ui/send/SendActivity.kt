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
import com.sender.ui.send.fragments.received.ReceiveList
import com.sender.ui.send.fragments.sents.SentList
import com.sender.util.Utils
import com.sender.util.networkUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class SendActivity : AppCompatActivity() {

    private val thingsToSend = mutableSetOf<TransferFile>()
    private val sendListFragment = SentList()
    private val receiveListFragment = ReceiveList()
    var host1 : Host? = null
    var host2 : Host? = null

    var client1 : Client? = null
    var client2 : Client? = null

    private val sectionsPagerAdapter = SectionsPagerAdapter(
        this,
        supportFragmentManager,
        sendListFragment,
        receiveListFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

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
                if (host){ initHosts() }
                else{ initClients() }
            findViewById<MaterialButton>(R.id.sentItems).setOnClickListener {
                if (thingsToSend.size<=0)return@setOnClickListener
                if (host){ handleHost()
                }else{ handleClient() }
            }
        }
    }

    private fun initHosts(){
        Thread{
            host1 = Host(itemsToSend = toArrayList(thingsToSend),
                context = this,
                ip=networkUtils.getDeviceIpAddress(),
                port = networkUtils.getPort()
            )
        }.start()
        Thread{
            host2 = Host(itemsToSend = toArrayList(thingsToSend),
                context = this,
                ip=networkUtils.getDeviceIpAddress(),
                port = networkUtils.getPort()+1
            )
            host2?.receive()
        }.start()

        Thread{
            while (true){
                Thread.sleep(1000)
                runOnUiThread {
                    Log.e("omo wtf","is happening inside loop ${host1?.getSending()} ${host2?.getReceiving()}")
                    if (host1!=null){
                        val adapter = sendListFragment.getSendingAdapter()
                        if (adapter.getData()==host1?.getSending()){
                            adapter.updateData(host1?.getSending())
                        }
                    }
                    if (host2!=null){
                        val adapter = receiveListFragment.getReceivingAdapter()
                        if (adapter.getData()==host2?.getReceiving()){
                            adapter.updateData(host2?.getReceiving())
                        }

                    }
                }
            }
        }.start()
    }

    private  fun initClients(){
        Thread{
            while (true){
                try {
                    client1 = Client(itemsToSend = toArrayList(thingsToSend),
                        context = this,
                        ip=networkUtils.getHostIpFromClient(),
                        port = networkUtils.getPort()
                    )
                    break
                }catch (e : Exception){

                }
            }
            client1?.receive()
        }.start()

        Thread{
            while (true){
                try {
                    client2 = Client(itemsToSend = toArrayList(thingsToSend),
                        context = this,
                        ip=networkUtils.getHostIpFromClient(),
                        port = networkUtils.getPort()+1
                    )
                    break
                }catch (e : Exception){

                }
            }
        }.start()

        Thread{
            while (true){
                Thread.sleep(1000)
                runOnUiThread {
                    Log.e("omo wtf","is happening inside loop ${client2?.getSending()} ${client1?.getReceiving()}")
                    if (client1!=null){
                        val adapter = sendListFragment.getSendingAdapter()
                        if (adapter.getData()!=client2?.getSending()){
                            adapter.updateData(client2?.getSending())
                        }
                    }
                    if (client2!=null){
                        val adapter = receiveListFragment.getReceivingAdapter()
                        if (adapter.getData()!=client1?.getReceiving()){
                            adapter.updateData(client1?.getReceiving())
                        }
                    }
                }
            }
        }.start()
    }

    private fun handleHost(){
        Thread{
            val ts = toArrayList(thingsToSend)
            runOnUiThread {
                thingsToSend.clear()
                findViewById<MaterialButton>(R.id.sentItems).text=
                    "SEND(${sizeOfThingsToSend()})"
                sectionsPagerAdapter.clearTracker()
            }
            host1?.updateSendingItems(ts)
            host1?.send(ts)
        }.start()

    }

    private fun handleClient(){
        Thread{
            val ts = toArrayList(thingsToSend)
            runOnUiThread {
                thingsToSend.clear()
                findViewById<MaterialButton>(R.id.sentItems).text=
                    "SEND(${sizeOfThingsToSend()})"
                sectionsPagerAdapter.clearTracker()
            }
            client2?.updateSendingItems(ts)
            client2?.send(ts)
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