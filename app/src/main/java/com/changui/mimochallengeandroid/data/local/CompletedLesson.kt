package com.changui.mimochallengeandroid.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "CompletedLesson")
@Parcelize
data class CompletedLesson(
    @PrimaryKey
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int = -1,
    @ColumnInfo(name = "start_date")
    val startDate: String?,
    @ColumnInfo(name = "end_date")
    val endDate: String? = null
): Parcelable
