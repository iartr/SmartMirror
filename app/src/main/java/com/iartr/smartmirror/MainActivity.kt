package com.iartr.smartmirror

import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import com.iartr.smartmirror.mvvm.BaseActivity
import com.iartr.smartmirror.mirror.MirrorFragment

class MainActivity : BaseActivity(R.layout.activity_main) {
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        com.iartr.smartmirror.design.R.id.fragment_container
        fragmentContainer = findViewById(fragmentContainerId)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(fragmentContainerId, MirrorFragment())
                .commit()
        }
    }

    private val fragmentContainerId = com.iartr.smartmirror.design.R.id.fragment_container
}