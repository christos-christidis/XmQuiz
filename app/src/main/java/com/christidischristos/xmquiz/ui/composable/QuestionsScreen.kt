package com.christidischristos.xmquiz.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.christidischristos.xmquiz.R
import com.christidischristos.xmquiz.ui.theme.Typography
import com.christidischristos.xmquiz.ui.theme.XmQuizTheme
import com.christidischristos.xmquiz.viewmodel.QuestionState
import com.christidischristos.xmquiz.viewmodel.QuestionsViewModel
import com.christidischristos.xmquiz.viewmodel.UiState

@Composable
fun QuestionsScreen(
    onBackClicked: () -> Unit,
    viewModel: QuestionsViewModel
) {
    val questionState by viewModel.questionState
    val uiState by viewModel.uiState

    QuestionsScreenContent(
        onBackClicked = onBackClicked,
        questionState = questionState,
        uiState = uiState,
        onPreviousClicked = viewModel::onPreviousClicked,
        onNextClicked = viewModel::onNextClicked,
        onAnswerChanged = viewModel::onAnswerChanged,
        onSubmitClicked = viewModel::onSubmitClicked,
        onDismissDialog = viewModel::onDismissDialog,
        onRetryClicked = viewModel::onRetryClicked
    )
}

@Composable
fun QuestionsScreenContent(
    onBackClicked: () -> Unit,
    questionState: QuestionState,
    uiState: UiState,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onAnswerChanged: (String) -> Unit,
    onSubmitClicked: () -> Unit,
    onDismissDialog: () -> Unit,
    onRetryClicked: () -> Unit
) {
    XmQuizTheme {
        Surface(color = Color.LightGray) {
            Column {
                ButtonRow(
                    onBackClicked = onBackClicked,
                    questionNumber = questionState.questionNumber,
                    onPreviousClicked = onPreviousClicked,
                    previousButtonEnabled = uiState.previousButtonEnabled,
                    onNextClicked = onNextClicked,
                    nextButtonEnabled = uiState.nextButtonEnabled
                )
                MainContent(
                    totalQuestionsSubmitted = questionState.totalQuestionsSubmitted,
                    questionText = questionState.questionText,
                    questionAnswer = questionState.questionAnswer,
                    onAnswerChanged = onAnswerChanged,
                    textFieldEnabled = uiState.textFieldEnabled,
                    onSubmitClicked = onSubmitClicked,
                    submitButtonEnabled = uiState.submitButtonEnabled
                )

            }
        }
        SuccessOrFailureDialog(
            submitSuccessful = uiState.submitSuccessful,
            onDismissDialog = onDismissDialog,
            onRetryClicked = onRetryClicked
        )
    }
}

@Composable
private fun MainContent(
    totalQuestionsSubmitted: String,
    questionText: String,
    questionAnswer: String,
    onAnswerChanged: (String) -> Unit,
    textFieldEnabled: Boolean,
    onSubmitClicked: () -> Unit,
    submitButtonEnabled: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(
                    R.string.questions_submitted,
                    totalQuestionsSubmitted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .wrapContentSize(align = Alignment.Center),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = questionText,
                style = Typography.h5.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = questionAnswer,
                onValueChange = onAnswerChanged,
                enabled = textFieldEnabled,
                placeholder = {
                    Text(text = stringResource(R.string.type_here_for_an_answer))
                }
            )
            Spacer(modifier = Modifier.height(128.dp))
            Button(
                onClick = onSubmitClicked,
                enabled = submitButtonEnabled,
            ) {
                Text(
                    text = stringResource(
                        if (submitButtonEnabled) R.string.submit else R.string.already_submitted
                    ),
                    modifier = Modifier.padding(horizontal = 64.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ButtonRow(
    onBackClicked: () -> Unit,
    questionNumber: String,
    onPreviousClicked: () -> Unit,
    previousButtonEnabled: Boolean,
    onNextClicked: () -> Unit,
    nextButtonEnabled: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClicked) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(R.string.navigate_to_initial_screen)
            )
        }
        Text(
            text = stringResource(R.string.question_number, questionNumber),
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(),
            fontWeight = FontWeight.ExtraBold
        )
        TextButton(
            onClick = onPreviousClicked,
            enabled = previousButtonEnabled
        ) {
            Text(stringResource(R.string.previous))
        }
        TextButton(onClick = onNextClicked, enabled = nextButtonEnabled) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
private fun SuccessOrFailureDialog(
    submitSuccessful: Boolean?,
    onDismissDialog: () -> Unit,
    onRetryClicked: () -> Unit
) {
    if (submitSuccessful != null) {
        Dialog(onDismissRequest = onDismissDialog) {
            Surface(color = if (submitSuccessful == true) Color.Green else Color.Red) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 64.dp)
                ) {
                    Text(
                        text = stringResource(if (submitSuccessful == true) R.string.success else R.string.failure),
                        modifier = Modifier.weight(1f)
                    )
                    if (submitSuccessful == false) {
                        OutlinedButton(onClick = onRetryClicked) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun QuestionsScreenPreview() {
    QuestionsScreenContent(
        onBackClicked = {},
        questionState = QuestionState(
            questionNumber = "1/20",
            totalQuestionsSubmitted = "0",
            questionText = "What is your favourite food?",
            questionAnswer = "My favourite food is salad"
        ),
        uiState = UiState(
            previousButtonEnabled = true,
            nextButtonEnabled = true,
            textFieldEnabled = true,
            submitButtonEnabled = true,
            submitSuccessful = null
        ),
        onPreviousClicked = {},
        onNextClicked = {},
        onAnswerChanged = {},
        onSubmitClicked = {},
        onDismissDialog = {},
        onRetryClicked = {}
    )
}
