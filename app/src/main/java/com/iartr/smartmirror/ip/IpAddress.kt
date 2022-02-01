package com.iartr.smartmirror.ip

import java.net.NetworkInterface

class IpAddress {
    fun get(): String {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces().toList()
        for (intf in networkInterfaces) {
            val addrs = intf.inetAddresses.toList()
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val hostAddresses = addr.hostAddress?.uppercase() ?: continue
                    val delim = hostAddresses.indexOf('%')
                    return if (delim < 0) hostAddresses else hostAddresses.substring(0, delim)
                }
            }
        }
        return ""
    }
}