package com.christidischristos.xmquiz.fakedata

import com.christidischristos.xmquiz.network.NetworkQuestion
import com.christidischristos.xmquiz.repo.RepoQuestion
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

object FakeData {

    val networkQuestions = listOf(
        NetworkQuestion(1, "a"),
        NetworkQuestion(2, "aa"),
        NetworkQuestion(3, "aaa"),
    )

    val repoQuestions = networkQuestions.map {
        RepoQuestion(id = it.id, question = it.question)
    }

    val httpException = HttpException(Response.error<ResponseBody>(400, "".toResponseBody()))
}