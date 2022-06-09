package com.iartr.smartmirror.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.iartr.smartmirror.R

class WeekTempFragment: Fragment() {

    private lateinit var dayButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weektemp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dayButton = view.findViewById(R.id.dayButton)
        dayButton.setOnClickListener { openDay() }
    }

    companion object{
        @JvmStatic
        fun newInstance() = WeekTempFragment()
    }

    private fun openDay() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addToBackStack(null)
            ?.replace(R.id.placeholder, DayTempFragment.newInstance())
            ?.commit()
    }
}