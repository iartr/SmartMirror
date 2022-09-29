package com.iartr.smartmirror.di

import android.content.Context
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.accountsettings.AccountSettingsFeatureDependencies
import com.iartr.smartmirror.camera.FacesReceiveTask
import com.iartr.smartmirror.core.utils.dagger.AppScope
import com.iartr.smartmirror.toggles.ITogglesRepository
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class, BindsModule::class])
@AppScope
interface AppComponent : AccountSettingsFeatureDependencies {

    override val accountRepository: IAccountRepository
    override val togglesRepository: ITogglesRepository
    val facesReceiveTask: FacesReceiveTask

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }

}