package com.sender.ui.send

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sender.R
import com.sender.ui.send.fragments.PlaceholderFragment
import com.sender.ui.send.fragments.apps.AppList
import com.sender.ui.send.fragments.audio.AudioList
import com.sender.ui.send.fragments.docs.DocumentList
import com.sender.ui.send.fragments.images.ImageList
import com.sender.ui.send.fragments.videos.VideoList

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4,
    R.string.tab_text_5
)

class SectionsPagerAdapter(private val context: Context,
                           fm: FragmentManager
) :
    FragmentPagerAdapter(fm) {

    private val appList = AppList()
    private val videoList = VideoList()
    private val documentList = DocumentList()
    private val imageList = ImageList()
    private val audioList = AudioList()

    override fun getItem(position: Int): Fragment {
        when (position) {
            0->{
                return appList
            }
            1->{
                return videoList
            }
            2->{
                return documentList
            }
            3->{
                return imageList
            }
            4->{
                return audioList
            }
            else->{
                return PlaceholderFragment.newInstance(position + 1)
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    fun clearTracker(){
        imageList.clearTracker()
        appList.clearTracker()
        audioList.clearTracker()
        documentList.clearTracker()
        videoList.clearTracker()
    }
}