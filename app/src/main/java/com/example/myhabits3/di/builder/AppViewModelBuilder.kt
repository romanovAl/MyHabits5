package com.example.myhabits3.di.builder

import androidx.lifecycle.ViewModel
import com.example.myhabits3.di.ViewModelKey
import com.example.myhabits3.ui.viewModels.AddEditViewModel
import com.example.myhabits3.ui.viewModels.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AppViewModelBuilder {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddEditViewModel::class)
    abstract fun bindAddEditViewModel(addEditViewModel: AddEditViewModel): ViewModel

}