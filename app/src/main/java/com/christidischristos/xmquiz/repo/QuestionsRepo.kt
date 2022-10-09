package com.christidischristos.xmquiz.repo

interface QuestionsRepo {

    suspend fun fetchQuestions(): ApiResult<List<RepoQuestion>>

    suspend fun submitQuestion(
        questionId: Int,
        questionAnswer: String
    ): ApiResult<Unit>
}