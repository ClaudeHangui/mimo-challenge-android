package com.changui.mimochallengeandroid.domain.usecase

import com.changui.mimochallengeandroid.domain.repository.LessonsRepository
import io.reactivex.Single
import javax.inject.Inject

interface RestartGameUseCase {
    fun clearCacheForCompletedLessons(): Single<Boolean>
}

class RestartGameUseCaseImpl @Inject constructor(private var repository: LessonsRepository): RestartGameUseCase {
    override fun clearCacheForCompletedLessons(): Single<Boolean> {
        return repository.clearAllCompletedLessons()
    }

}