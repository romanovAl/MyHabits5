package com.example.myhabits3.di.builder

import dagger.Binds
import dagger.Module
import repository.AppRepository
import ru.romanoval.data.repository.AppRepoImpl

@Module
abstract class RepositoryBuilder {

    @Binds
    abstract fun bindsHabitsRepository(repoImpl: AppRepoImpl): AppRepository

}