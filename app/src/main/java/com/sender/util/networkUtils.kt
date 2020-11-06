package com.sender.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter.formatIpAddress
import androidx.annotation.RequiresApi
import java.lang.reflect.Method
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

class networkUtils {
    companion object {
        fun getWifiIP():String{
            return  "0.0.0.0" //TODO fix me
        }

        fun getHostSpotIP():String{ //TODO fix ,e
            return  "0.0.0.0"
        }

        fun getPort():Int{
            return  57678
        }

        fun getDeviceIpAddress():String{
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            var networkInterface : NetworkInterface
            var inetAddress : InetAddress
            while (networkInterfaces.hasMoreElements()){
                networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()){
                    inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.indexOf(":")<0){
                        if (!inetAddress.hostAddress.startsWith("192"))continue
                        return  inetAddress.hostAddress
                    }
                }
            }
            return  "0.0.0.0"
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun findWlanNetwork(context: Context): Network? {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val etherNetwork: Network? = null
            for (network in connectivityManager.allNetworks) {
                val networkInfo = connectivityManager.getNetworkInfo(network)
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return network
                }
            }
            return null
        }

        fun getHostIpFromClient():String{
            val ip = getDeviceIpAddress()
            if (ip == "0.0.0.0"){
                return  ip
            }
            var ipSplit = ip.split(".")
            ipSplit = ipSplit.subList(0,ipSplit.size-1)
            return  "${ipSplit.joinToString(".")}.1"
        }

        fun isMobileHotspot(context: Context): Boolean {
            val manager = (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            try {
                val method: Method = manager.javaClass.getDeclaredMethod("isWifiApEnabled")
                method.isAccessible = true
                return (method.invoke(manager) as Boolean)
            } catch (ignored: Throwable) {
            }
            return false
        }

        fun isWifiOn(context: Context):Boolean{
            val wifiMgr =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiMgr.connectionInfo
            return wifiInfo.supplicantState.name=="COMPLETED"
        }
    }
}