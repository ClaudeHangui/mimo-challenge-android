package com.changui.mimochallengeandroid.domain.usecase

import com.changui.mimochallengeandroid.data.local.CompletedLesson
import com.changui.mimochallengeandroid.data.local.LocalLesson
import com.changui.mimochallengeandroid.domain.ResultState
import com.changui.mimochallengeandroid.domain.entity.LessonContent
import com.changui.mimochallengeandroid.domain.repository.LessonsRepository
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

interface GetLessonsUseCase {
    fun getAllLessons(): Single<ResultState<List<LessonContent>>>
}

class GetLessonsUseCaseImpl @Inject constructor(private var repository: LessonsRepository) :
    GetLessonsUseCase {
    override fun getAllLessons(): Single<ResultState<List<LessonContent>>> {
        return Single.zip(
            repository.getAllLessons(),
            repository.getAllCompletedLessons(),
            { activeLessons: ResultState<List<LocalLesson>>, completedLessons: List<CompletedLesson> ->
                if (activeLessons is ResultState.Error) {
                    ResultState.Error(activeLessons.error)
                } else {
                    // get mimo lessons who have not yet been solved
                    val activeLes = activeLessons as ResultState.Success
                    val filterItems = activeLes.data.filter { lessonItem -> !completedLessons.any { it.lessonId == lessonItem.id && it.endDate != null } }
                    if (filterItems.isNullOrEmpty()) {
                        ResultState.Success(listOf())
                    } else {
                        val items = filterItems.map { unsolvedLesson ->
                            if (unsolvedLesson.input == null)
                                LessonContent.NonEditableContent(
                                    unsolvedLesson.id,
                                    unsolvedLesson.content
                                )
                            else LessonContent.EditableContent(
                                unsolvedLesson.id,
                                unsolvedLesson.content,
                                unsolvedLesson.input.startIndex,
                                unsolvedLesson.input.endIndex
                            )
                        }
                        ResultState.Success(items)
                    }
                }
            }).doOnSuccess {
            if (it is ResultState.Success) {
                it.data.forEach { lessonContent ->
                    when (lessonContent) {
                        is LessonContent.NonEditableContent -> repository.saveLessonStartDate(
                            lessonContent.id,
                            Date().time.toString()
                        )
                        is LessonContent.EditableContent -> repository.saveLessonStartDate(
                            lessonContent.id,
                            Date().time.toString()
                        )
                    }
                }
            }
        }

    }
}