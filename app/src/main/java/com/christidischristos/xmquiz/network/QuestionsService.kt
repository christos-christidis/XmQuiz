package com.christidischristos.xmquiz.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface QuestionsService {
    @GET("questions")
    suspend fun getQuestions(): List<NetworkQuestion>

    @POST("question/submit")
    suspend fun submitQuestion(
        @Body answer: NetworkAnswer
    ): Response<Unit>
}