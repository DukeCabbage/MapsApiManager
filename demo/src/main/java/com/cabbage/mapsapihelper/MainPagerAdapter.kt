package com.cabbage.mapsapihelper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import timber.log.Timber

class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        Timber.v("getItem $position")
        when (position) {
            0 -> return FirstFragment()
            1 -> return ResultListFragment()
            else -> throw IllegalArgumentException("Index not supported")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}