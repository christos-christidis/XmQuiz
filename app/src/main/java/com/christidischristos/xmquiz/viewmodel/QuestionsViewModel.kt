package com.christidischristos.xmquiz.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christidischristos.xmquiz.repo.ApiResult
import com.christidischristos.xmquiz.repo.QuestionsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val repo: QuestionsRepo
) : ViewModel() {

    private val _questionState = mutableStateOf(QuestionState())
    val questionState: State<QuestionState> = _questionState

    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    private var currentIndex: Int = 0
    private var questions: List<Question> = emptyList()

    init {
        fetchQuestions()
    }

    fun onNextClicked() {
        currentIndex += 1
        updateQuestionTexts()
        updateUiState()
    }

    fun onPreviousClicked() {
        currentIndex -= 1
        updateQuestionTexts()
        updateUiState()
    }

    fun onAnswerChanged(text: String) {
        questions[currentIndex].answer = text
        _questionState.value = questionState.value.copy(questionAnswer = text)
        _uiState.value = uiState.value.copy(
            submitButtonEnabled = text.isNotBlank()
        )
    }

    fun onSubmitClicked() {
        viewModelScope.launch {
            val currentQuestion = questions[currentIndex]
            when (repo.submitQuestion(currentQuestion.id, currentQuestion.answer)) {
                is ApiResult.Error -> {
                    showTemporaryDialog(false)
                }
                is ApiResult.Success -> {
                    questions[currentIndex].submitted = true
                    updateUiState()
                    updateQuestionTexts()
                    showTemporaryDialog(true)
                }

            }
        }
    }

    fun onDismissDialog() {
        _uiState.value = uiState.value.copy(submitSuccessful = null)
    }

    fun onRetryClicked() {
        _uiState.value = uiState.value.copy(submitSuccessful = null)
        onSubmitClicked()
    }

    private fun fetchQuestions() {
        viewModelScope.launch {
            when (val result = repo.fetchQuestions()) {
                is ApiResult.Error -> {
                }
                is ApiResult.Success -> {
                    questions = result.data.map { Question(id = it.id, text = it.question) }
                    if (questions.isNotEmpty()) {
                        currentIndex = 0
                        updateQuestionTexts()
                        updateUiState()
                    }
                }
            }
        }
    }

    private suspend fun showTemporaryDialog(submitSuccessful: Boolean) {
        _uiState.value = uiState.value.copy(submitSuccessful = submitSuccessful)
        delay(3000)
        _uiState.value = uiState.value.copy(submitSuccessful = null)
    }

    private fun updateQuestionTexts() {
        _questionState.value = QuestionState(
            questionNumber = "${currentIndex + 1}/${questions.size}",
            totalQuestionsSubmitted = "${questions.count { it.submitted }}",
            questionText = questions[currentIndex].text,
            questionAnswer = questions[currentIndex].answer
        )
    }

    private fun updateUiState() {
        val currentQuestion = questions[currentIndex]
        _uiState.value = UiState(
            previousButtonEnabled = currentIndex > 0,
            nextButtonEnabled = currentIndex < questions.size - 1,
            textFieldEnabled = currentQuestion.submitted.not(),
            submitButtonEnabled = currentQuestion.submitted.not() && currentQuestion.answer.isNotBlank(),
            submitSuccessful = null
        )
    }
}

data class QuestionState(
    val questionNumber: String = "??/??",
    val totalQuestionsSubmitted: String = "??",
    val questionText: String = "",
    val questionAnswer: String = ""
)

data class UiState(
    val previousButtonEnabled: Boolean = false,
    val nextButtonEnabled: Boolean = false,
    val textFieldEnabled: Boolean = false,
    val submitButtonEnabled: Boolean = false,
    val submitSuccessful: Boolean? = null
)