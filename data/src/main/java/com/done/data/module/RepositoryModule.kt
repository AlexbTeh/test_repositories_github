package com.done.data.module

import com.done.data.repository.GithubRepoImpl
import com.done.domain.repository.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule{

    @Binds
    fun bindGithubRepository(githubRepoImpl: GithubRepoImpl): GithubRepository

}