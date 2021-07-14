package com.changui.mimochallengeandroid.data.remote

import io.reactivex.Single
import retrofit2.http.GET

interface MimoApiService {
    @GET("api/lessons")
    fun getMimoLessons(): Single<MimoLessonsRemoteModel?>
}