package com.iartr.smartmirror

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.iartr.smartmirror.ui.base.BaseActivity
import com.iartr.smartmirror.ui.main.MainFragment

class MainActivity : BaseActivity(R.layout.activity_main) {
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentContainer = findViewById(R.id.fragment_container_view)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, MainFragment())
                .commit()
        }
    }
}