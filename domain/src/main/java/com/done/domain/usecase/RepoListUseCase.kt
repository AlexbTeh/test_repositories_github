package com.done.domain.usecase

import com.done.domain.repository.GithubRepository
import com.done.domain.utils.ApiUseCaseParams
import com.done.domain.utils.Result
import com.done.domain.models.RepoItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepoListUseCase @Inject constructor(
    private val repository: GithubRepository
): ApiUseCaseParams<RepoListUseCase.Params, List<RepoItemEntity>> {
    override suspend fun execute(params: Params): Flow<Result<List<RepoItemEntity>>> {
        return repository.fetchRepoList(params)
    }
    data class Params(val userName:String)
}