package com.iartr.smartmirror.di

import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.camera.FacesReceiveTask
import com.iartr.smartmirror.deps.AccountRepository
import com.iartr.smartmirror.deps.FirebaseBasedFacesReceiveTask
import com.iartr.smartmirror.deps.FirebaseBasedTogglesRepository
import com.iartr.smartmirror.toggles.ITogglesRepository
import dagger.Binds
import dagger.Module

@Module
interface BindsModule {

    @Binds
    fun bindAccountRepository(repository: AccountRepository): IAccountRepository

    @Binds
    fun bindTogglesRepository(repository: FirebaseBasedTogglesRepository): ITogglesRepository

    @Binds
    fun bindFacesReceiveTask(impl: FirebaseBasedFacesReceiveTask): FacesReceiveTask
}