package com.example.myhabits3.di.builder

import com.example.myhabits3.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityProviders {

    @ContributesAndroidInjector
    abstract fun provideMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun provideFragmentGoodHabits(): FragmentGoodHabits

    @ContributesAndroidInjector
    abstract fun provideFragmentBadHabits(): FragmentBadHabits

    @ContributesAndroidInjector
    abstract fun provideFragmentAddEdit(): FragmentAddEdit

    @ContributesAndroidInjector
    abstract fun provideColorPickerDialogFragment(): ColorPickerDialogFragment


}