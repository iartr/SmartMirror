package com.iartr.smartmirror.ui.currency.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iartr.smartmirror.ui.currency.graph.CurrencyGraphFragment
import com.iartr.smartmirror.ui.currency.list.CurrencyListFragment

class PagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val fragments = mutableListOf<Fragment?>(null, null)

    override fun createFragment(position: Int): Fragment {
        if (position < 0 || position >= fragments.size) throw IllegalArgumentException("Invalid fragment position")
        if (fragments[position] == null) fragments[position] = getFragment(position)
        return fragments[position]!!
    }

    private fun getFragment(position: Int) = when (position) {
        0 -> CurrencyListFragment()
        1 -> CurrencyGraphFragment()
        else -> throw IllegalArgumentException("Invalid fragment position")
    }

    override fun getItemCount() = fragments.size
}