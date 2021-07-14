package com.changui.mimochallengeandroid.data.remote

import com.changui.mimochallengeandroid.data.GetLessonsRemoteDataStore
import com.changui.mimochallengeandroid.domain.ResultState
import com.changui.mimochallengeandroid.domain.resultify
import io.reactivex.Single
import javax.inject.Inject

class GetLessonsRemoteDataStoreImpl  @Inject constructor (private val api: MimoApiService,
                                                         private val errorFactory: GetLessonsErrorFactory)
    : GetLessonsRemoteDataStore {



    override fun getRemoteLessons(): Single<ResultState<MimoLessonsRemoteModel?>> {
        return api.getMimoLessons().resultify(errorFactory)
    }

}