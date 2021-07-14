package com.changui.mimochallengeandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface LessonsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllActiveLessons(activeLessons: List<LocalLesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActiveLesson(activeLesson: LocalLesson)

    @Query("update CompletedLesson set end_date = :endDate where lesson_id = :id")
    fun updateLessonEndDate(id: Int, endDate: String): Completable

    @Query("select * from CompletedLesson")
    fun getAllCompletedLessons(): Single<List<CompletedLesson>>

    @Query("select * from ActiveLesson")
    fun getAllActiveLessons(): Single<List<LocalLesson>>

    @Query("delete from CompletedLesson")
    fun deleteAllCompletedLessons(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessonStartDate(completedLesson: CompletedLesson)
}