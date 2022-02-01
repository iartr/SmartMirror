package com.iartr.smartmirror.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.iartr.smartmirror.R

class RetryingErrorView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var errorTitle: TextView
    private var retryButton: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.error_layout, this, true)

        errorTitle = findViewById(R.id.error_layout_title)
        retryButton = findViewById(R.id.error_layout_retry_button)
    }

    fun showUntilStartRetryAction(@StringRes message: Int = R.string.common_network_error, retryAction: () -> Unit) {
        show(message, retryAction, object : RetryingErrorListener {
            override fun onShowed() {

            }

            override fun onActionStarted() {
                hide()
            }
        })
    }

    fun show(
        @StringRes message: Int = R.string.common_network_error,
        retryAction: () -> Unit,
        listener: RetryingErrorListener = object : RetryingErrorListener { }
    ) {
        errorTitle.setText(message)
        retryButton.setOnClickListener {
            listener.onActionStarted()
            retryAction()
        }
        this.isVisible = true
        listener.onShowed()
    }

    fun hide() {
        this.isVisible = false
    }
}

interface RetryingErrorListener {
    // Suitable case: hide other views on screen
    fun onShowed() { }

    // Suitable cases: show loading, block retry button, hide error view
    fun onActionStarted() {  }
}
