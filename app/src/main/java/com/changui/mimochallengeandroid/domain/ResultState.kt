package com.changui.mimochallengeandroid.domain

import com.changui.mimochallengeandroid.data.remote.RemoteBaseFailureFactory
import io.reactivex.Single

sealed class ResultState<T> {
    data class Error<T>(val error: Failure) : ResultState<T>()
    data class Success<T>(val data: T) : ResultState<T>()
}

fun <T> Single<T>.resultify(failureFactory: RemoteBaseFailureFactory): Single<ResultState<T>> =
    map<ResultState<T>> { ResultState.Success(it) }
        .onErrorReturn { ResultState.Error(failureFactory.produce(it as Exception)) }
