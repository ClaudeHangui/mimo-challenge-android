package com.changui.mimochallengeandroid.data.local

import com.changui.mimochallengeandroid.data.CompletedLessonLocalDataStore
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class CompletedLessonLocalDataStoreImpl  @Inject constructor (private val dao: LessonsDao): CompletedLessonLocalDataStore {

    override fun saveCompletedLesson(lessonId: Int, lessonEndDate: String) : Completable{
        return dao.updateLessonEndDate(lessonId, lessonEndDate)
    }

    override fun getCompletedLessons(): Single<List<CompletedLesson>> {
        return dao.getAllCompletedLessons()
    }

    override fun clearAllCompletedLessons(): Completable {
        return dao.deleteAllCompletedLessons()
    }

    override fun saveLessonStartDate(lessonId: Int, lessonStartDateTime: String) {
        dao.insertLessonStartDate(CompletedLesson(lessonId, lessonStartDateTime))
    }
}