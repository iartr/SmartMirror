package com.iartr.smartmirror

import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import com.iartr.smartmirror.mvvm.BaseActivity
import com.iartr.smartmirror.mirror.MirrorFragment

class MainActivity : BaseActivity(R.layout.activity_main) {
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentContainer = findViewById(R.id.fragment_container)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MirrorFragment())
                .commit()
        }
    }
}