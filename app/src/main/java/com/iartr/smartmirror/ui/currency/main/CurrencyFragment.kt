package com.iartr.smartmirror.ui.currency.main

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.base.BaseFragment
import com.iartr.smartmirror.ui.currency.adapter.PagerAdapter

class CurrencyFragment : BaseFragment(R.layout.fragment_currency) {

    private lateinit var tabs: TabLayout
    private lateinit var pages: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields(view)
    }

    private fun initFields(view: View) {
        tabs = view.findViewById(R.id.tabs)
        pages = view.findViewById(R.id.pages)
        pages.isUserInputEnabled = false
        pages.adapter = PagerAdapter(this)
        TabLayoutMediator(tabs, pages) { tab, index ->
            tab.text = when(index) {
                1 -> "История"
                else -> "Текущие"
            }
        }.attach()
    }

    companion object {
        fun newInstance() = CurrencyFragment()
    }
}