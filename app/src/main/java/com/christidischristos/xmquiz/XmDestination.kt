package com.christidischristos.xmquiz

sealed class XmDestination(
    val route: String
)

object InitialScreenDestination : XmDestination(route = "initial")

object QuestionsScreenDestination : XmDestination(route = "questions")