package com.christidischristos.xmquiz.repo

import com.christidischristos.xmquiz.network.NetworkAnswer
import com.christidischristos.xmquiz.network.NetworkQuestion
import com.christidischristos.xmquiz.network.QuestionsService
import retrofit2.HttpException
import javax.inject.Inject

class QuestionsRepoImpl @Inject constructor(
    private val service: QuestionsService
) : QuestionsRepo {

    override suspend fun fetchQuestions(): ApiResult<List<RepoQuestion>> {
        return try {
            val questions = service.getQuestions()
            ApiResult.Success(questions.asDomainList())
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    override suspend fun submitQuestion(
        questionId: Int,
        questionAnswer: String
    ): ApiResult<Unit> {
        return try {
            val response = service.submitQuestion(NetworkAnswer(questionId, questionAnswer))
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    private fun List<NetworkQuestion>.asDomainList(): List<RepoQuestion> {
        return this.map {
            RepoQuestion(id = it.id, question = it.question)
        }
    }
}