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
import com.sender.ui.send.fragments.received.ReceiveList
import com.sender.ui.send.fragments.sents.SentList

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4,
    R.string.tab_text_5,
    R.string.tab_text_6,
    R.string.tab_text_7
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0->{
                return AppList()
            }
            1->{
                return VideoList()
            }
            2->{
                return DocumentList()
            }
            3->{
                return ImageList()
            }
            4->{
                return AudioList()
            }
            5->{
                return SentList()
            }
            6->{
                return ReceiveList()
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
}