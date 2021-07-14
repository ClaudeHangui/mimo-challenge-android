package com.changui.mimochallengeandroid.data.local

import com.changui.mimochallengeandroid.data.ActiveLessonsLocalDataStore
import io.reactivex.Single
import javax.inject.Inject

class ActiveLessonsLocalDataStoreImpl @Inject constructor(private val dao: LessonsDao) : ActiveLessonsLocalDataStore {

    override fun saveActiveLessons(lessons: List<LocalLesson>) {
        dao.insertAllActiveLessons(lessons)
    }

    override fun saveActiveLesson(lesson: LocalLesson) {
        dao.insertActiveLesson(lesson)
    }

    override fun getActiveLessons(): Single<List<LocalLesson>> {
        return dao.getAllActiveLessons().onErrorReturn { listOf() }
    }

}