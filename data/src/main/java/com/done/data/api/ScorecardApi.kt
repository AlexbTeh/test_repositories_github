package com.done.data.api

import com.done.data.models.CreateRoundRequest
import com.done.data.models.PostScoresRequest
import com.done.data.models.RoundResponse
import com.done.data.models.SimpleResponse
import com.done.data.models.UpdateRoundRequest
import retrofit2.http.*

interface ScorecardApi {
    @POST("rounds")
    suspend fun createRound(@Body body: CreateRoundRequest): RoundResponse

    @PUT("rounds/{id}")
    suspend fun updateRound(@Path("id") id: Int, @Body body: UpdateRoundRequest): RoundResponse

    @POST("rounds/{id}/submit")
    suspend fun postScores(@Path("id") id: Int, @Body body: PostScoresRequest): SimpleResponse
}

