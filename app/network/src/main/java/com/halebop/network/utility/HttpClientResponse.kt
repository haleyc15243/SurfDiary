package com.halebop.network.utility

import retrofit2.Response

sealed class HttpClientResponse<out T> {
    data class Success<T>(val data: T): HttpClientResponse<T>()
    data class Exception(val e: Throwable): HttpClientResponse<Nothing>()
    data class Error(
        val code: Int,
        val errorBody: String?,
        val requestTraceId: RequestTraceId? = null
    ) : HttpClientResponse<Nothing>()
    @JvmInline
    value class RequestTraceId(val value: String)
}

private fun <T> Response<T>.toError(): HttpClientResponse.Error {
    return HttpClientResponse.Error(
        code = code(),
        errorBody = errorBody()?.source()?.readString(Charsets.UTF_8),
        requestTraceId = headers()["Request-Trace-Id"]?.let { HttpClientResponse.RequestTraceId(it) }
    )
}

internal inline fun <T> httpClientResponse(body: () -> Response<T>): HttpClientResponse<T> = runCatching(body).map {
    when {
        it.isSuccessful -> HttpClientResponse.Success(it.body()!!)
        else -> it.toError()
    }
}.getOrElse { HttpClientResponse.Exception(it) }