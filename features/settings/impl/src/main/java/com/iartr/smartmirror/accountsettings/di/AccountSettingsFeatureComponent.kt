package com.iartr.smartmirror.accountsettings.di

import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import com.iartr.smartmirror.accountsettings.AccountSettingFeatureApi
import com.iartr.smartmirror.accountsettings.AccountSettingsFeatureDependencies
import com.iartr.smartmirror.accountsettings.AccountSettingsFragment
import com.iartr.smartmirror.core.utils.dagger.Feature
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlin.properties.Delegates

@Component(
    modules = [AccountSettingsFeatureModule::class],
    dependencies = [AccountSettingsFeatureDependencies::class]
)
@Feature
interface AccountSettingsFeatureComponent : AccountSettingFeatureApi {
    override fun fragment(): Fragment

    fun inject(fragment: AccountSettingsFragment)

    @Component.Factory
    interface Factory {
        fun create(
            deps: AccountSettingsFeatureDependencies
        ): AccountSettingsFeatureComponent
    }
}

@Module
class AccountSettingsFeatureModule {
    @Provides
    fun providesFragment(): Fragment = AccountSettingsFragment.newInstance()
}

lateinit var accountSettingsFeatureComponentProvider: Lazy<AccountSettingsFeatureComponent>

interface AccountSettingsFeatureDependenciesProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: AccountSettingsFeatureDependencies

    companion object : AccountSettingsFeatureDependenciesProvider by AccountSettingsFeatureDependenciesStore
}

object AccountSettingsFeatureDependenciesStore : AccountSettingsFeatureDependenciesProvider {
    override var deps: AccountSettingsFeatureDependencies by Delegates.notNull()
}