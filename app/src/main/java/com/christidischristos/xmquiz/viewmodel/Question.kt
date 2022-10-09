package com.christidischristos.xmquiz.viewmodel

data class Question(
    val id: Int,
    val text: String,
    var answer: String = "",
    var submitted: Boolean = false
)
