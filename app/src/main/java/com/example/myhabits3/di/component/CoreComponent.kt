package com.example.myhabits3.di.component

import android.app.Application
import com.example.myhabits3.core.App
import com.example.myhabits3.di.builder.ActivityBuilder
import com.example.myhabits3.di.modules.ContextModule
import com.example.myhabits3.di.modules.DatabaseModule
import com.example.myhabits3.di.modules.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ActivityBuilder::class,
        ContextModule::class,
        DatabaseModule::class,
        AndroidSupportInjectionModule::class
    ]
)
interface CoreComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): CoreComponent
    }

}