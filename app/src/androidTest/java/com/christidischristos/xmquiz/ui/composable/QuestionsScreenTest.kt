package com.christidischristos.xmquiz.ui.composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.christidischristos.xmquiz.viewmodel.QuestionState
import com.christidischristos.xmquiz.viewmodel.QuestionsViewModel
import com.christidischristos.xmquiz.viewmodel.UiState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// TODO: add more states, also a good idea would be to add paparazzi snapshot tests...
class QuestionsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @MockK
    lateinit var viewModel: QuestionsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockViewModelStateFuncs()
        composeTestRule.setContent {
            QuestionsScreen(onBackClicked = {}, viewModel = viewModel)
        }
    }

    @Test
    fun stateWhereAnswerIsReadyToBeSubmitted() {
        composeTestRule.apply {
            onNodeWithContentDescription("Navigate to initial screen").assertIsDisplayed()
            onNodeWithText("Question 1/20").assertIsDisplayed()
            onNodeWithText("Previous").assert(isNotEnabled())
            onNodeWithText("Next").assertIsEnabled()
            onNodeWithText("What?").assertIsDisplayed()
            onNodeWithText("That").assertIsDisplayed()
            onNodeWithText("Submit").assertIsEnabled()
        }
    }

    private fun mockViewModelStateFuncs() {
        every { viewModel.uiState } returns mutableStateOf(
            UiState(
                previousButtonEnabled = false,
                nextButtonEnabled = true,
                textFieldEnabled = true,
                submitButtonEnabled = true,
                submitSuccessful = null
            )
        )
        every { viewModel.questionState } returns mutableStateOf(
            QuestionState(
                questionNumber = "1/20",
                totalQuestionsSubmitted = "0",
                questionText = "What?",
                questionAnswer = "That"
            )
        )
    }
}