package com.iartr.smartmirror.camera

import android.hardware.display.DisplayManager

/**
 * We need a display listener for orientation changes that do not trigger a configuration
 * change, for example if we choose to override config change in manifest or for 180-degree
 * orientation changes.
 */
internal class DisplayListener(
    private val displayId: Int,
    private val onDisplayChanged: () -> Unit
) : DisplayManager.DisplayListener {

    override fun onDisplayAdded(displayId: Int) = Unit

    override fun onDisplayRemoved(displayId: Int) = Unit

    override fun onDisplayChanged(displayId: Int) {
        if (displayId == this.displayId) {
            onDisplayChanged()
        }
    }
}