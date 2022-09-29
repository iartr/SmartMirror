package com.iartr.smartmirror.mirror

import androidx.recyclerview.widget.ListAdapter
import com.iartr.smartmirror.account.IAccountRepository
import com.iartr.smartmirror.accountsettings.AccountSettingFeatureApi
import com.iartr.smartmirror.camera.FacesReceiveTask
import com.iartr.smartmirror.currency.ICurrencyRepository
import com.iartr.smartmirror.news.INewsRepository
import com.iartr.smartmirror.news.News
import com.iartr.smartmirror.toggles.ITogglesRepository
import com.iartr.smartmirror.weather.IWeatherRepository

interface MirrorFeatureDependencies {
    val weatherRepository: IWeatherRepository

    val newsRepository: INewsRepository
    val newsRecyclerAdapter: ListAdapter<News, *>

    val currencyRepository: ICurrencyRepository
    val togglesRepository: ITogglesRepository
    val accountRepository: IAccountRepository

    val accountSettingsApi: AccountSettingFeatureApi

    val facesReceiveTask: FacesReceiveTask
}