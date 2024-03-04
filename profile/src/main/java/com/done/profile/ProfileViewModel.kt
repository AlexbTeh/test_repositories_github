package com.done.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.domain.models.ProfileEntity
import com.done.domain.usecase.ProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.done.domain.utils.Result

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase
): ViewModel(){
    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileUiState get() = _profileUiState.asStateFlow()


    init {
        fetchProfile()
    }

    private fun fetchProfile(){
        viewModelScope.launch {
            profileUseCase.execute(ProfileUseCase.Params(userName = "thedeveloperworldisyours")).collect{ response->
                when(response){
                    is Result.Error -> _profileUiState.value =
                        ProfileUiState.Error(response.message)
                    Result.Loading -> _profileUiState.value = ProfileUiState.Loading
                    is Result.Success -> _profileUiState.value =
                        ProfileUiState.Success(response.data)
                }
            }
        }
    }

    fun handleAction(action: ProfileUiAction){
        when(action){
            ProfileUiAction.FetchProfile -> fetchProfile()
        }
    }
}



sealed interface ProfileUiState{
    data object Loading : ProfileUiState
    data class Success(val data: ProfileEntity): ProfileUiState
    data class Error(val message:String) : ProfileUiState
}

sealed interface ProfileUiAction{
    data object FetchProfile: ProfileUiAction
}