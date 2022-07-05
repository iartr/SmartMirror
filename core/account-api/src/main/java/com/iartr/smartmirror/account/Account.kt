package com.iartr.smartmirror.account

data class Account(
    val uid: String,
    val displayName: String?,
    val photoUrl: String?,
    val email: String?,
    val isEmailVerified: Boolean,
    val phone: String?,
)