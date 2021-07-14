package com.changui.mimochallengeandroid.data

import com.changui.mimochallengeandroid.data.local.LocalLesson
import com.changui.mimochallengeandroid.data.local.CompletedLesson
import com.changui.mimochallengeandroid.data.scheduler.RxSchedulersImpl
import com.changui.mimochallengeandroid.domain.ResultState
import com.changui.mimochallengeandroid.domain.repository.LessonsRepository
import io.reactivex.Single
import javax.inject.Inject

class LessonRepositoryImpl  @Inject constructor (
    private val remoteDataStore: GetLessonsRemoteDataStore,
    private val activeLessonsLocalDataStore: ActiveLessonsLocalDataStore,
    private val completedLessonLocalDataStore: CompletedLessonLocalDataStore,
    private val rxSchedulers: RxSchedulersImpl,
    private val lessonsMapper: RemoteToLocalLessonsMapper
): LessonsRepository {

    private fun remoteObservable(): Single<ResultState<List<LocalLesson>>> {

        val eitherResponse = remoteDataStore.getRemoteLessons()
        return eitherResponse.map {
            when(it) {
                is ResultState.Error -> ResultState.Error(it.error)
                is ResultState.Success -> {
                    val localResponse = lessonsMapper.mapToUI(it.data?.lessons)
                    ResultState.Success(localResponse)
                }
            }
        }.doOnSuccess {
            if (it is ResultState.Success)
                activeLessonsLocalDataStore.saveActiveLessons(it.data)
        }.doFinally {
            activeLessonsLocalDataStore.getActiveLessons()
        }
}

    private fun localObservable(): Single<List<LocalLesson>> = activeLessonsLocalDataStore.getActiveLessons()

    override fun getAllLessons(): Single<ResultState<List<LocalLesson>>> =
        Single.zip(localObservable(), remoteObservable(),
            { localObs: List<LocalLesson>, remoteObs: ResultState<List<LocalLesson>> ->
            if (localObs.isNullOrEmpty()) {
                if (remoteObs is ResultState.Error) ResultState.Error(remoteObs.error)
                else remoteObs as ResultState.Success
            }
            else {
                ResultState.Success(localObs)
            }
        }).subscribeOn(rxSchedulers.io)

    override fun getAllCompletedLessons(): Single<List<CompletedLesson>> =
        completedLessonLocalDataStore.getCompletedLessons().subscribeOn(rxSchedulers.io)

    override fun saveLessonStartDate(lessonId: Int, date: String) {
        completedLessonLocalDataStore.saveLessonStartDate(lessonId, date)
    }

    override fun saveCompletedLesson(lessonId: Int, lessonEndDateTime: String): Single<Boolean> {
        return completedLessonLocalDataStore.saveCompletedLesson(lessonId, lessonEndDateTime)
            .andThen(Single.just(true)).subscribeOn(rxSchedulers.io)
    }

    override fun clearAllCompletedLessons(): Single<Boolean> {
        return completedLessonLocalDataStore.clearAllCompletedLessons()
            .andThen(Single.just(true)).subscribeOn(rxSchedulers.io)
    }

}