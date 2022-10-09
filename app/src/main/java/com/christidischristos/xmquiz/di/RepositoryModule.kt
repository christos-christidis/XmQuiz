package com.christidischristos.xmquiz.di

import com.christidischristos.xmquiz.repo.QuestionsRepo
import com.christidischristos.xmquiz.repo.QuestionsRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun provideRepo(repoImpl: QuestionsRepoImpl): QuestionsRepo
}