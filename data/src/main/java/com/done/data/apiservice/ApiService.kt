package com.done.data.apiservice

import com.done.domain.response.ProfileApiResponse
import com.done.domain.response.RepoItemApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/users/{username}/repos")
    suspend fun fetchRepoList(
        @Path("username")username:String
    ): Response<List<RepoItemApiResponse>>

    @GET("/users/{username}")
    suspend fun fetchProfile(
        @Path("username")username:String
    ):Response<ProfileApiResponse>
}