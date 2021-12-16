package com.iartr.smartmirror.data.account

interface IAccountRepository {
    fun saveAccount(account: Account)

    fun getAccount(): Account
}