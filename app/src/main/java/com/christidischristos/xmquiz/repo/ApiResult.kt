package com.christidischristos.xmquiz.repo

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val throwable: Throwable) : ApiResult<T>()
}