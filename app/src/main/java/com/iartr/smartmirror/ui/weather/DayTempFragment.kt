package com.iartr.smartmirror.ui.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.account.AccountFragment
import kotlin.concurrent.fixedRateTimer

class DayTempFragment : Fragment() {

    private lateinit var weekButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daytemp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weekButton = view.findViewById(R.id.weekButton)
        weekButton.setOnClickListener { openWeek() }
    }

    companion object{
        @JvmStatic
        fun newInstance() = DayTempFragment()
    }

    private fun openWeek() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addToBackStack(null)
            ?.replace(R.id.placeholder, WeekTempFragment.newInstance())
            ?.commit()
    }
}