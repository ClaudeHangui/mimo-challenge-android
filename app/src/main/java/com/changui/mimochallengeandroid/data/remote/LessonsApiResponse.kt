package com.changui.mimochallengeandroid.data.remote

import com.changui.mimochallengeandroid.data.local.Content
import com.changui.mimochallengeandroid.data.local.Input


data class MimoLessonsRemoteModel(
    val lessons: List<RemoteLesson>?
)

data class RemoteLesson(
    val content: List<Content>,
    val id: Int,
    val input: Input? = null
)