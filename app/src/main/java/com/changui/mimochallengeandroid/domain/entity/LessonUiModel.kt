package com.changui.mimochallengeandroid.domain.entity

import android.os.Parcelable
import com.changui.mimochallengeandroid.data.local.Content
import kotlinx.parcelize.Parcelize

sealed class LessonContent: Parcelable {
    @Parcelize data class NonEditableContent(val id: Int, val content: List<Content>) : LessonContent()
    @Parcelize data class EditableContent(val id: Int, val content: List<Content>, val startIndex: Int, val endIndex: Int) : LessonContent()
}