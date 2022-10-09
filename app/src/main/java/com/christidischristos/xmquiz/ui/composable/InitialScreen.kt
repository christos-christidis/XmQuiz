package com.christidischristos.xmquiz.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.christidischristos.xmquiz.R
import com.christidischristos.xmquiz.ui.theme.Typography
import com.christidischristos.xmquiz.ui.theme.XmQuizTheme

@Composable
fun InitialScreen(onStartSurveyClicked: () -> Unit) {
    InitialScreenContent(onStartSurveyClicked = onStartSurveyClicked)
}

@Composable
fun InitialScreenContent(onStartSurveyClicked: () -> Unit) {
    XmQuizTheme {
        Surface(color = Color.LightGray) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.welcome),
                    style = Typography.h6,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.TopCenter)
                )
                Button(
                    onClick = onStartSurveyClicked,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = stringResource(R.string.start_survey),
                        modifier = Modifier.padding(horizontal = 64.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun InitialScreenPreview() {
    InitialScreenContent(onStartSurveyClicked = {})
}