package com.done.domain.usecase

import com.done.domain.repository.GithubRepository
import com.done.domain.utils.ApiUseCaseParams
import com.done.domain.utils.Result
import com.done.domain.models.ProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val repository: GithubRepository
): ApiUseCaseParams<ProfileUseCase.Params, ProfileEntity> {
    data class Params(val userName:String)
    override suspend fun execute(params: Params): Flow<Result<ProfileEntity>> {
        return repository.fetchProfile(params)
    }
}