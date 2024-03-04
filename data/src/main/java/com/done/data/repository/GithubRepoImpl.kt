package com.done.data.repository

import com.done.data.apiservice.ApiService
import com.done.data.mapper.ProfileMapper
import com.done.data.mapper.RepoListItemMapper
import com.done.data.utils.NetworkBoundResource
import com.done.data.utils.mapFromApiResponse
import com.done.domain.models.ProfileEntity
import com.done.domain.models.RepoItemEntity
import com.done.domain.repository.GithubRepository
import com.done.domain.usecase.ProfileUseCase
import com.done.domain.usecase.RepoListUseCase
import com.done.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GithubRepoImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkBoundResources: NetworkBoundResource,
    private val repositoryListItemMapper: RepoListItemMapper,
    private val profileMapper: ProfileMapper
): GithubRepository {

    override suspend fun fetchRepoList(params: RepoListUseCase.Params): Flow<Result<List<RepoItemEntity>>> {
        return mapFromApiResponse(
            result = networkBoundResources.downloadData {
                apiService.fetchRepoList(params.userName)
            },repositoryListItemMapper
        )
    }

    override suspend fun fetchProfile(params: ProfileUseCase.Params): Flow<Result<ProfileEntity>> {
        return mapFromApiResponse(
            result = networkBoundResources.downloadData {
                apiService.fetchProfile(params.userName)
            },profileMapper
        )
    }

}