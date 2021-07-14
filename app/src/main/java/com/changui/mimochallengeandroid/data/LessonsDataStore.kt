package com.changui.mimochallengeandroid.data

import com.changui.mimochallengeandroid.data.local.LocalLesson
import com.changui.mimochallengeandroid.data.local.CompletedLesson
import com.changui.mimochallengeandroid.data.remote.MimoLessonsRemoteModel
import com.changui.mimochallengeandroid.domain.ResultState
import io.reactivex.Completable
import io.reactivex.Single

interface GetLessonsRemoteDataStore {
    fun getRemoteLessons(): Single<ResultState<MimoLessonsRemoteModel?>>
}

interface CompletedLessonLocalDataStore {
    fun saveCompletedLesson(lessonId: Int, lessonEndDate: String): Completable
    fun getCompletedLessons(): Single<List<CompletedLesson>>
    fun clearAllCompletedLessons(): Completable
    fun saveLessonStartDate(lessonId: Int, lessonStartDateTime: String)
}

interface ActiveLessonsLocalDataStore {
    fun saveActiveLessons(lessons: List<LocalLesson>)
    fun saveActiveLesson(lesson: LocalLesson)
    fun getActiveLessons(): Single<List<LocalLesson>>
}