package com.done.domain.repository

import com.done.domain.usecase.ProfileUseCase
import com.done.domain.usecase.RepoListUseCase
import com.done.domain.models.ProfileEntity
import com.done.domain.models.RepoItemEntity
import kotlinx.coroutines.flow.Flow
import com.done.domain.utils.Result


interface GithubRepository {
    suspend fun fetchRepoList(params: RepoListUseCase.Params): Flow<Result<List<RepoItemEntity>>>
    suspend fun fetchProfile(params: ProfileUseCase.Params):Flow<Result<ProfileEntity>>
}