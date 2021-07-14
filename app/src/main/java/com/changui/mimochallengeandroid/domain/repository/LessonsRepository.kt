package com.changui.mimochallengeandroid.domain.repository

import com.changui.mimochallengeandroid.data.local.LocalLesson
import com.changui.mimochallengeandroid.data.local.CompletedLesson
import com.changui.mimochallengeandroid.domain.ResultState
import io.reactivex.Single

interface LessonsRepository {
    fun saveCompletedLesson(lessonId: Int, lessonEndDateTime: String): Single<Boolean>
    fun clearAllCompletedLessons(): Single<Boolean>
    fun getAllLessons(): Single<ResultState<List<LocalLesson>>>
    fun getAllCompletedLessons(): Single<List<CompletedLesson>>
    fun saveLessonStartDate(lessonId: Int, date: String)
}