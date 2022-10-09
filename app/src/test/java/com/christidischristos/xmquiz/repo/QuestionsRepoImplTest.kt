package com.christidischristos.xmquiz.repo

import com.christidischristos.xmquiz.fakedata.FakeData
import com.christidischristos.xmquiz.network.NetworkAnswer
import com.christidischristos.xmquiz.network.QuestionsService
import com.christidischristos.xmquiz.viewmodel.Question
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionsRepoImplTest {

    @MockK
    lateinit var service: QuestionsService

    private lateinit var subject: QuestionsRepoImpl

    private val submittedAnswer = NetworkAnswer(1, "a")

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        subject = QuestionsRepoImpl(service)
        coEvery { service.getQuestions() } returns FakeData.networkQuestions
        coEvery { service.submitQuestion(submittedAnswer) } returns
            Response.success<Unit>(200, null)
    }

    @Test
    fun `fetchQuestions() - WHEN service returns questions THEN repo returns them in ApiResult Success`() {
        runTest {
            val result = subject.fetchQuestions()
            assertThat(result).isEqualTo(ApiResult.Success(FakeData.repoQuestions))
        }
    }

    @Test
    fun `fetchQuestions() - WHEN service throws IOException THEN repo returns ApiResult Error`() {
        val e = IOException()
        coEvery { service.getQuestions() } throws e
        runTest {
            val result = subject.fetchQuestions()
            assertThat(result).isEqualTo(ApiResult.Error<List<Question>>(e))
        }
    }

    @Test
    fun `fetchQuestions() - WHEN service throws HttpException THEN repo returns ApiResult Error`() {
        coEvery { service.getQuestions() } throws FakeData.httpException
        runTest {
            val result = subject.fetchQuestions()
            assertThat(result).isEqualTo(ApiResult.Error<List<Question>>(FakeData.httpException))
        }
    }

    @Test
    fun `submitQuestion() - WHEN service returns 200 status response THEN repo returns ApiResult Success`() {
        runTest {
            val result = subject.submitQuestion(submittedAnswer.id, submittedAnswer.answer)
            assertThat(result).isEqualTo(ApiResult.Success(Unit))
        }
    }

    @Test
    fun `submitQuestion() - WHEN service returns 400 status response THEN repo returns ApiResult Error`() {
        val response = Response.error<Unit>(400, "".toResponseBody())
        coEvery { service.submitQuestion(submittedAnswer) } returns response
        runTest {
            val result = subject.submitQuestion(submittedAnswer.id, submittedAnswer.answer)
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        }
    }
}