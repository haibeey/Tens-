package com.sender.ui.send.fragments.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sender.R
import com.sender.ui.send.ItemSelection
import com.sender.ui.send.fragments.audio.RvAudioItems
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class AppList : Fragment() {
    private var tracker: SelectionTracker<Long>? = null
    private var  allList =  ArrayList<RvAppItems>()
    private val viewAdapter = AppListAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewLayout = inflater.inflate(R.layout.fragment_app_list, container, false)
        val viewManager = GridLayoutManager(context,4)

        if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState);
        }

        fun loadData(){
            GlobalScope.launch {
                val pm: PackageManager? = activity?.packageManager
                val packages = pm?.getInstalledPackages(0)

                val  appList =  ArrayList<RvAppItems>()

                if (packages != null) {
                    for (packageInfo in packages) {
                        if (packageInfo.versionName==null ||
                            (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM)!==0 ||
                            (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) !== 0
                        )
                        {
                            continue
                        }

                        val rvAppItems = RvAppItems()
                        rvAppItems.appName = "${pm.getApplicationLabel(packageInfo.applicationInfo)}.apk"

                        rvAppItems.packageName = packageInfo.packageName
                        rvAppItems.uri =  Uri.fromFile(File(packageInfo.applicationInfo.publicSourceDir))

                        appList.add(rvAppItems)

                        if (appList.size%50==0){
                            appList.sortBy { it.appName }
                            activity?.runOnUiThread {
                                viewAdapter.addTen(appList)
                            }
                            appList.clear()
                        }
                        allList.add(rvAppItems)
                    }

                    allList.sortBy { it.appName }
                    activity?.runOnUiThread {
                        viewAdapter.update(allList)
                    }
                }
            }
        }
        if (allList.isNotEmpty()){
            viewAdapter.update(allList)
        }else{
            if (savedInstanceState != null) {
                try {
                    allList =
                        savedInstanceState.getSerializable("app_data") as ArrayList<RvAppItems>
                }catch (e : java.lang.Exception){
                    loadData()
                }
            }else{
                loadData()
            }
        }

        viewLayout.findViewById<RecyclerView>(R.id.rv_app_list).apply {
            layoutManager = viewManager
            adapter = viewAdapter

            tracker = SelectionTracker.Builder<Long>(
                "mySelection", this,
                StableIdKeyProvider(this), ItemSelection(this),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()
            viewAdapter.tracker = tracker
        }
        return  viewLayout
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
        outState.putSerializable("app_data",allList)
    }

    fun clearTracker(){
        try {
            viewAdapter.tracker?.clearSelection()
        }catch(e: Exception){}
    }
}
