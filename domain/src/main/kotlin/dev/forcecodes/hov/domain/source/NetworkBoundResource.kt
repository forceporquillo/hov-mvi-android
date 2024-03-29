package dev.forcecodes.hov.domain.source

import dev.forcecodes.hov.core.Result
import dev.forcecodes.hov.core.internal.Logger
import dev.forcecodes.hov.data.api.*
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource {

    protected inline fun <Cache, Remote> conflateResource(
        requireLoad: Boolean = true,
        crossinline cacheSource: () -> Flow<Cache>,
        crossinline remoteSource: suspend () -> ApiResponse<Remote>,
        crossinline saveFetchResult: suspend (Remote) -> Unit,
        crossinline shouldFetch: (Cache) -> Boolean = { true }
    ): Flow<Result<Cache>> = flow {
        if (requireLoad) {
            emit(Result.Loading())
        }
        val cache = cacheSource().first()

        val conflated = if (shouldFetch(cache)) {
            emit(Result.Loading(cache))
            try {
                val request = remoteSource()
                request.onSuccess {
                    saveFetchResult(this)
                }.onError { errorMessage, exception ->
                    Logger.e(exception, errorMessage)
                    throw ApiErrorResponse(errorMessage)
                }.onEmpty { emptyMessage ->
                    Logger.e(emptyMessage)
                    throw EmptyResponseException(emptyMessage)
                }
                cacheSource().map { Result.Success(it) }
            } catch (e: Exception) {
                Logger.e(e)
                cacheSource().map { Result.Error(e, it) }
            }
        } else {
            cacheSource().map { Result.Success(it) }
        }

        emitAll(conflated)
    }
}