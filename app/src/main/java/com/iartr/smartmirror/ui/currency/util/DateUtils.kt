package com.iartr.smartmirror.ui.currency.util

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun LocalDate.asDate() = Date.from(this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())