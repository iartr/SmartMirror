package com.iartr.smartmirror.news.di

import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.ListAdapter
import com.iartr.smartmirror.core.utils.dagger.Feature
import com.iartr.smartmirror.network.retrofitApi
import com.iartr.smartmirror.news.News
import com.iartr.smartmirror.news.NewsAdapter
import com.iartr.smartmirror.news.INewsRepository
import com.iartr.smartmirror.news.NewsApi
import com.iartr.smartmirror.news.NewsFeatureDependencies
import com.iartr.smartmirror.news.NewsRepository
import com.iartr.smartmirror.news.NewsFeatureApi
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlin.properties.Delegates

@Component(
    modules = [NewsFeatureModule::class, NewsBindsModule::class],
    dependencies = [NewsFeatureDependencies::class]
)
@Feature
interface NewsFeatureComponent : NewsFeatureApi {
    override fun newsRepository(): INewsRepository
    override fun recyclerAdapter(): ListAdapter<News, *>

    @Component.Factory
    interface Factory {
        fun create(
            deps: NewsFeatureDependencies
        ): NewsFeatureComponent
    }

}

@Module
internal class NewsFeatureModule {
    @Provides
    fun provideNewsRemoteSource(): NewsApi {
        return retrofitApi("https://newsapi.org/v2/")
    }
}

@Module
internal interface NewsBindsModule {
    @Binds
    fun bindNewsRepository(repository: NewsRepository): INewsRepository

    @Binds
    fun bindRecyclerAdapter(adapter: NewsAdapter): ListAdapter<News, *>
}

// -----

interface NewsFeatureDependenciesProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: NewsFeatureDependencies

    companion object : NewsFeatureDependenciesProvider {
        override var deps: NewsFeatureDependencies by Delegates.notNull()
    }
}