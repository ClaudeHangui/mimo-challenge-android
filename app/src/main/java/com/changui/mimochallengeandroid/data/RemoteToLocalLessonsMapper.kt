package com.changui.mimochallengeandroid.data

import com.changui.mimochallengeandroid.data.local.LocalLesson
import com.changui.mimochallengeandroid.data.remote.RemoteLesson
import com.changui.mimochallengeandroid.domain.Mapper

class RemoteToLocalLessonsMapper: Mapper<List<RemoteLesson>?, List<LocalLesson>> {
    override fun mapToUI(input: List<RemoteLesson>?): List<LocalLesson> {
        return input?.map {
            LocalLesson(it.id, it.input, it.content)
        } ?: listOf()
    }
}