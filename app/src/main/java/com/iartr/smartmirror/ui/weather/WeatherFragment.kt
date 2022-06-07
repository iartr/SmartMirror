package com.iartr.smartmirror.ui.weather

import android.os.Bundle
import android.view.View
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.account.AccountFragment
import com.iartr.smartmirror.ui.base.BaseFragment

class WeatherFragment : BaseFragment(R.layout.fragment_weather){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    companion object {
        fun newInstance() = WeatherFragment()
    }
}