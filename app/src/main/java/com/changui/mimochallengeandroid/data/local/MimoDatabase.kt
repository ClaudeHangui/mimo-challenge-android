package com.changui.mimochallengeandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LocalLesson::class, CompletedLesson::class], version = 1)
@TypeConverters(LessonContentConverter::class)
abstract class MimoDatabase: RoomDatabase() {
    abstract fun lessonsDao(): LessonsDao
}