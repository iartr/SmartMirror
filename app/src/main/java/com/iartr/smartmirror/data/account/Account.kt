package com.iartr.smartmirror.data.account

data class Account(
    val uid: String,
    val displayName: String?,
    val photoUrl: String?,
    val email: String?,
    val isEmailVerified: Boolean,
    val phone: String?,
)