package com.christidischristos.xmquiz.viewmodel

import com.christidischristos.xmquiz.fakedata.FakeData
import com.christidischristos.xmquiz.repo.ApiResult
import com.christidischristos.xmquiz.repo.QuestionsRepoImpl
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var repo: QuestionsRepoImpl

    private lateinit var subject: QuestionsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this)
        coEvery { repo.fetchQuestions() } returns ApiResult.Success(FakeData.repoQuestions)
        coEvery { repo.submitQuestion(any(), any()) } returns ApiResult.Success(Unit)
        subject = QuestionsViewModel(repo)
        dispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `after init, check that uiState and questionState are correct`() {
        subject = QuestionsViewModel(repo)
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { repo.fetchQuestions() }
        assertUiState(
            previousButton = false, nextButton = true, textField = true,
            submitButton = false, submitSuccess = null
        )
        assertQuestionTexts(questionIndex = 0)
    }

    @Test
    fun `onNextClicked() - update texts and ui correctly`() {
        subject.onNextClicked()

        assertUiState(
            previousButton = true, nextButton = true, textField = true,
            submitButton = false, submitSuccess = null
        )
        assertQuestionTexts(questionIndex = 1)
    }

    @Test
    fun `onNextClicked() - if last question, update texts and ui correctly`() {
        subject.onNextClicked()
        subject.onNextClicked()

        assertUiState(
            previousButton = true, nextButton = false, textField = true,
            submitButton = false, submitSuccess = null
        )
        assertQuestionTexts(questionIndex = 2)
    }

    @Test
    fun `onPreviousClicked() - update texts and ui correctly`() {
        subject.onNextClicked()
        subject.onPreviousClicked()

        assertUiState(
            previousButton = false, nextButton = true, textField = true,
            submitButton = false, submitSuccess = null
        )
        assertQuestionTexts(questionIndex = 0)
    }

    @Test
    fun `onAnswerChanged() - update texts and ui correctly`() {
        subject.onAnswerChanged("foo")

        assertUiState(
            previousButton = false, nextButton = true, textField = true,
            submitButton = true, submitSuccess = null
        )
        assertQuestionTexts(questionIndex = 0, withAnswer = "foo")
    }

    @Test
    fun `onSubmitClicked() - on success, update texts and ui correctly`() {
        subject.onAnswerChanged("bar")
        subject.onSubmitClicked()

        dispatcher.scheduler.advanceTimeBy(5)
        assertUiState(
            previousButton = false, nextButton = true, textField = false,
            submitButton = false, submitSuccess = true
        )
        assertQuestionTexts(questionIndex = 0, withAnswer = "bar", withSubmitted = 1)

        dispatcher.scheduler.advanceTimeBy(3000)
        assertUiState(
            previousButton = false, nextButton = true, textField = false,
            submitButton = false, submitSuccess = null
        )
    }

    @Test
    fun `onSubmitClicked() - on failure, update texts and ui correctly`() {
        coEvery { repo.submitQuestion(any(), any()) } returns
            ApiResult.Error(FakeData.httpException)

        subject.onAnswerChanged("bar")
        subject.onSubmitClicked()

        dispatcher.scheduler.advanceTimeBy(5)
        assertUiState(
            previousButton = false, nextButton = true, textField = true,
            submitButton = true, submitSuccess = false
        )
        assertQuestionTexts(questionIndex = 0, withAnswer = "bar")

        dispatcher.scheduler.advanceTimeBy(3000)
        assertUiState(
            previousButton = false, nextButton = true, textField = true,
            submitButton = true, submitSuccess = null
        )
    }

    @Test
    fun `onDismissDialog - update texts and ui correctly`() {
        subject.onAnswerChanged("baz")
        subject.onSubmitClicked()

        dispatcher.scheduler.advanceTimeBy(5)
        assertUiState(
            previousButton = false, nextButton = true, textField = false,
            submitButton = false, submitSuccess = true
        )
        assertQuestionTexts(questionIndex = 0, withAnswer = "baz", withSubmitted = 1)

        subject.onDismissDialog()
        dispatcher.scheduler.advanceTimeBy(5)
        assertUiState(
            previousButton = false, nextButton = true, textField = false,
            submitButton = false, submitSuccess = null
        )
    }

    @Test
    fun `onRetryClicked() - update texts and ui correctly`() {
        coEvery { repo.submitQuestion(any(), any()) } returns
            ApiResult.Error(FakeData.httpException)

        subject.onAnswerChanged("buzz")
        subject.onSubmitClicked()

        dispatcher.scheduler.advanceTimeBy(5)
        assertUiState(
            previousButton = false, nextButton = true, textField = true,
            submitButton = true, submitSuccess = false
        )
        assertQuestionTexts(questionIndex = 0, withAnswer = "buzz")

        subject.onRetryClicked()
        val question = FakeData.repoQuestions[0]
        coVerify { repo.submitQuestion(question.id, "buzz") }
    }

    @Test
    fun `state of previous questions is preserved`() {
        subject.onAnswerChanged("answer1")
        subject.onSubmitClicked()
        dispatcher.scheduler.advanceUntilIdle()
        subject.onNextClicked()

        coEvery { repo.submitQuestion(any(), any()) } returns
            ApiResult.Error(FakeData.httpException)
        subject.onAnswerChanged("answer2")
        subject.onSubmitClicked()
        dispatcher.scheduler.advanceUntilIdle()
        subject.onNextClicked()

        subject.onPreviousClicked()
        assertUiState(
            previousButton = true, nextButton = true, textField = true,
            submitButton = true, submitSuccess = null
        )
        assertQuestionTexts(questionIndex = 1, withAnswer = "answer2", withSubmitted = 1)

        subject.onPreviousClicked()
        assertUiState(
            previousButton = false, nextButton = true, textField = false,
            submitButton = false, submitSuccess = null
        )
        assertQuestionTexts(questionIndex = 0, withAnswer = "answer1", withSubmitted = 1)
    }

    private fun assertUiState(
        previousButton: Boolean,
        nextButton: Boolean,
        textField: Boolean,
        submitButton: Boolean,
        submitSuccess: Boolean?
    ) {
        subject.uiState.value.apply {
            assertThat(previousButtonEnabled).isEqualTo(previousButton)
            assertThat(nextButtonEnabled).isEqualTo(nextButton)
            assertThat(textFieldEnabled).isEqualTo(textField)
            assertThat(submitButtonEnabled).isEqualTo(submitButton)
            assertThat(submitSuccessful).isEqualTo(submitSuccess)
        }
    }

    private fun assertQuestionTexts(
        questionIndex: Int,
        withAnswer: String = "",
        withSubmitted: Int = 0
    ) {
        subject.questionState.value.apply {
            assertThat(questionNumber).isEqualTo("${questionIndex + 1}/3")
            assertThat(totalQuestionsSubmitted).isEqualTo("$withSubmitted")
            val question = FakeData.repoQuestions[questionIndex]
            assertThat(questionText).isEqualTo(question.question)
            assertThat(questionAnswer).isEqualTo(withAnswer)
        }
    }
}