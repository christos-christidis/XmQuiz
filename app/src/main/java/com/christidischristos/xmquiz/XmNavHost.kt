package com.christidischristos.xmquiz

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.christidischristos.xmquiz.ui.composable.InitialScreen
import com.christidischristos.xmquiz.ui.composable.QuestionsScreen

@Composable
fun XmNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = InitialScreenDestination.route
    ) {
        composable(route = InitialScreenDestination.route) {
            InitialScreen(onStartSurveyClicked = {
                navController.navigate(QuestionsScreenDestination.route)
            })
        }
        composable(route = QuestionsScreenDestination.route) {
            QuestionsScreen(
                onBackClicked = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }
    }
}