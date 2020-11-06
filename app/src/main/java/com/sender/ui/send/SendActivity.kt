package com.sender.ui.send

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.sender.R
import com.sender.client.Client
import com.sender.host.Host
import com.sender.models.FileTransmission
import com.sender.models.TransferFile
import com.sender.ui.send.fragments.received.ReceivingAdapter
import com.sender.ui.send.fragments.sents.SendingAdapters
import com.sender.util.networkUtils
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class SendActivity : AppCompatActivity() {

    private val thingsToSend = mutableSetOf<TransferFile>()
    private var host1 : Host? = null
    private var host2 : Host? = null

    private var client1 : Client? = null
    private var client2 : Client? = null

    private val MIN_SWIPPING_DISTANCE = 2

    private lateinit var sendingReceivingView : View

    private val sectionsPagerAdapter = SectionsPagerAdapter(
        this,
        supportFragmentManager
    )

    private val sendingAdapter = SendingAdapters()
    private val receivingAdapter = ReceivingAdapter()

    private var lastScroll = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.M){
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.bindProcessToNetwork(networkUtils.findWlanNetwork(this))
        }

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 5
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        sendingReceivingView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).
                                inflate(R.layout.sending_reciever_layout, null, false)

        val gesture = GestureDetector(this,gestureDetector())
        sendingReceivingView.setOnTouchListener { _, event ->
            gesture.onTouchEvent(event)
            true
        }

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
            findViewById<MaterialButton>(R.id.send_receive_button).setOnClickListener {
                showReceivingSending()
            }
        }

        setUpReceivingSending()
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
                Thread.sleep(2000)
                if (host1!=null && host1?.getSending()!=null ){
                    if (equalFileTransList(sendingAdapter.getData(),host1?.getSending()!!)){
                        runOnUiThread {
                            sendingAdapter.updateData(host1?.getSending())
                        }
                    }
                }
                if (host2!=null && host2?.getReceiving()!=null){
                    if (equalFileTransList(receivingAdapter.getData(),host2?.getReceiving()!!)){
                        runOnUiThread {
                            receivingAdapter.updateData(host2?.getReceiving())
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
                }catch (e : Exception){}
            }
        }.start()

        Thread{
            while (true){
                Thread.sleep(2000)
                if (client2!=null && client2?.getSending()!=null){
                    if (equalFileTransList(sendingAdapter.getData(),client2?.getSending()!!)){
                        runOnUiThread {
                            sendingAdapter.updateData(client2?.getSending())
                        }
                    }
                }
                if (client1!=null && client1?.getReceiving()!=null){
                    if (equalFileTransList(receivingAdapter.getData(),client1?.getReceiving()!!)){
                        runOnUiThread {
                            receivingAdapter.updateData(client1?.getReceiving())
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
                showReceivingSending()
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
                showReceivingSending()
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

    private fun showReceivingSending(){
        val alert: AlertDialog.Builder = AlertDialog.Builder(this,R.style.PauseDialog)
        val linearLayoutManager1 = LinearLayoutManager(this)
        sendingReceivingView.findViewById<RecyclerView>(R.id.sending_list).apply {
            layoutManager = linearLayoutManager1
            adapter = sendingAdapter
        }

        val linearLayoutManager2 = LinearLayoutManager(this)
        sendingReceivingView.findViewById<RecyclerView>(R.id.receiving_list).apply {
            layoutManager = linearLayoutManager2
            adapter = receivingAdapter
        }

        alert.setView(sendingReceivingView)
        alert.setNegativeButton("Hide") { dialog, _ -> dialog.cancel() }

        if (sendingReceivingView.parent!=null)
            (sendingReceivingView.parent as ViewGroup).removeView(sendingReceivingView)

        val dialog=alert.show()
        dialog.window?.setBackgroundDrawableResource(R.color.colorWhite)
        val closeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        closeBtn.textSize = 20f
        closeBtn.setTextColor(resources.getColor(R.color.colorPrimaryDark))
    }

    private fun setUpReceivingSending(){
        val receiving = sendingReceivingView.findViewById<TextView>(R.id.receiving_text)
        val sending = sendingReceivingView.findViewById<TextView>(R.id.sending_text)

        sending.setOnClickListener { updateSending() }
        receiving.setOnClickListener { updateReceiving() }
    }

    private fun updateSending(){
        val receivingViewIndicator = sendingReceivingView.findViewById<ImageView>(R.id.receiving_indicator)
        val rvSending = sendingReceivingView.findViewById<RecyclerView>(R.id.sending_list)
        val rvReceiving =  sendingReceivingView.findViewById<RecyclerView>(R.id.receiving_list)

        rvSending.apply {
            adapter= sendingAdapter
            layoutManager = LinearLayoutManager(this@SendActivity)
        }

        val sendingViewIndicator = sendingReceivingView.findViewById<ImageView>(R.id.sending_indicator)

        sendingViewIndicator.visibility = View.VISIBLE
        rvSending.visibility= View.VISIBLE
        receivingViewIndicator.visibility = View.INVISIBLE
        rvReceiving.visibility =View.GONE
    }

    private fun updateReceiving(){
        val receivingViewIndicator = sendingReceivingView.findViewById<ImageView>(R.id.receiving_indicator)
        val rvSending = sendingReceivingView.findViewById<RecyclerView>(R.id.sending_list)
        val rvReceiving =  sendingReceivingView.findViewById<RecyclerView>(R.id.receiving_list)
        val sendingViewIndicator = sendingReceivingView.findViewById<ImageView>(R.id.sending_indicator)

        rvReceiving.apply {
            adapter= receivingAdapter
            layoutManager = LinearLayoutManager(this@SendActivity)
        }

        receivingViewIndicator.visibility = View.VISIBLE
        rvReceiving.visibility =View.VISIBLE
        sendingViewIndicator.visibility = View.INVISIBLE
        rvSending.visibility= View.GONE
    }

    inner class gestureDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(me: MotionEvent): Boolean {
            return true
        }
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (System.currentTimeMillis()-lastScroll<500){
                return false
            }
            if (e1?.x?.minus(e2?.x!!) ?: 0f > MIN_SWIPPING_DISTANCE)
            {
                updateReceiving()
            }
            else if (e2?.x?.minus(e1?.x!!) ?: 0f > MIN_SWIPPING_DISTANCE)
            {
                updateSending()
            }
            lastScroll = System.currentTimeMillis()
            return false
        }
    }

    private fun equalFileTransList(a: ArrayList<FileTransmission>,b: ArrayList<FileTransmission>):Boolean{
        if (a.size!=b.size)return false
        val aSet = setOf<Int>()
        a.forEach {
            aSet.plus(it.hashCode())
        }
        b.forEach {
            if (aSet.contains(it.hashCode()))return false
        }
        return true
    }
}