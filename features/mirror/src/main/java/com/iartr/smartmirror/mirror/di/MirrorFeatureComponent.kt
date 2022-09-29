package com.iartr.smartmirror.mirror.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import com.iartr.smartmirror.core.utils.dagger.Feature
import com.iartr.smartmirror.mirror.MirrorFeatureApi
import com.iartr.smartmirror.mirror.MirrorFeatureDependencies
import com.iartr.smartmirror.mirror.MirrorFragment
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlin.properties.Delegates

@Component(
    modules = [MirrorModule::class],
    dependencies = [MirrorFeatureDependencies::class]
)
@Feature
interface MirrorFeatureComponent : MirrorFeatureApi {
    override fun mirrorFragment(): MirrorFragment

    fun inject(fragment: MirrorFragment)

    @Component.Factory
    interface Factory {
        fun create(
            deps: MirrorFeatureDependencies
        ): MirrorFeatureComponent
    }
}

@Module
class MirrorModule {
    @Provides
    fun fragment(): MirrorFragment = MirrorFragment()
}

// ----
interface MirrorFeatureDependenciesProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: MirrorFeatureDependencies

    companion object : MirrorFeatureDependenciesProvider by MirrorFeatureDependenciesStore
}

object MirrorFeatureDependenciesStore : MirrorFeatureDependenciesProvider {
    override var deps: MirrorFeatureDependencies by Delegates.notNull()
}

class MirrorFeatureViewModel : ViewModel() {
    val mirrorComponent: MirrorFeatureComponent = DaggerMirrorFeatureComponent
        .factory()
        .create(
            deps = MirrorFeatureDependenciesProvider.deps
        )
}