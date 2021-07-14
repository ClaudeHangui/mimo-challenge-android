package com.changui.mimochallengeandroid.domain.usecase

import com.changui.mimochallengeandroid.domain.repository.LessonsRepository
import io.reactivex.Single
import javax.inject.Inject

interface CompleteLessonUseCase {
    fun completeLesson(lessonId: Int, lessonEndDateTime: String): Single<Boolean>
}

class CompleteLessonUseCaseImpl  @Inject constructor (private var repository: LessonsRepository) : CompleteLessonUseCase {
    override fun completeLesson(lessonId: Int, lessonEndDateTime: String): Single<Boolean> {
        return repository.saveCompletedLesson(lessonId, lessonEndDateTime)
    }
}