package com.example.myhabits3.di.builder

import androidx.lifecycle.ViewModelProvider
import com.example.myhabits3.di.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        (RepositoryBuilder::class),
        (AppViewModelBuilder::class)
    ]
)
abstract class ViewModelBuilder {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}