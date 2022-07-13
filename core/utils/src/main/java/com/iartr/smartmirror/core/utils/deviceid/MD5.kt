package com.iartr.smartmirror.core.utils.deviceid

import java.security.MessageDigest

object MD5 {
    private val hex = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val tmpBuilder = object : ThreadLocal<StringBuilder>() {
        override fun initialValue(): StringBuilder {
            return StringBuilder()
        }
    }


    @JvmStatic
    fun convert(h: String): String {
        try {
            val md = MessageDigest.getInstance("MD5")
            val md5 = md.digest(h.toByteArray(charset("UTF-8")))
            tmpBuilder.get().setLength(0)
            hex(md5)
            return tmpBuilder.get().toString()
        } catch (ignored: Exception) {
        }
        return ""
    }

    @JvmStatic
    private fun hex(b: ByteArray) {
        for (aB in b) {
            tmpBuilder.get().append(hex[aB.toInt() and (0xF0).toInt() shr 4])
            tmpBuilder.get().append(hex[aB.toInt() and (0x0F).toInt()])
        }
    }
}