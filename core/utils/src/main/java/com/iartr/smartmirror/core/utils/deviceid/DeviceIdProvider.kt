package com.iartr.smartmirror.core.utils.deviceid

import android.os.Build

object DeviceIdProvider {
    fun getDeviceId(): String {
        val sb = StringBuilder()
            .append(Build.PRODUCT)
            .append(Build.BOARD)
            .append(Build.BOOTLOADER)
            .append(Build.BRAND)
            .append(Build.DEVICE)
            .append(Build.DISPLAY)
            .append(Build.FINGERPRINT)
            .append(Build.HARDWARE)
            .append(Build.HOST)
            .append(Build.ID)
            .append(Build.MANUFACTURER)
            .append(Build.MODEL)
            .append(Build.PRODUCT)
            .append(Build.TAGS)
            .toString()
        return MD5.convert(sb)
    }
}