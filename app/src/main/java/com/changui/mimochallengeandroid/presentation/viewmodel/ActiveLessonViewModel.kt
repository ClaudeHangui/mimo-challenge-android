package com.changui.mimochallengeandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changui.mimochallengeandroid.data.scheduler.RxSchedulersImpl
import com.changui.mimochallengeandroid.domain.usecase.CompleteLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject
@HiltViewModel
class ActiveLessonViewModel@Inject constructor (private val rxSchedulers: RxSchedulersImpl,
                                                private val completeLessonUseCase: CompleteLessonUseCase): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var disposable: Disposable

    private val uiStateMutableLiveData: MutableLiveData<UiState> = MutableLiveData<UiState>()
    fun getUiStateLiveData(): LiveData<UiState> = uiStateMutableLiveData

    private val loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun getLoadingLiveData(): LiveData<Boolean> = loadingMutableLiveData


    fun solveLesson(lessonContentId: Int, endingDateTime: String) {
        loadingMutableLiveData.value = true
        disposable = completeLessonUseCase.completeLesson(lessonContentId, endingDateTime)
            .observeOn(rxSchedulers.main).subscribeBy {
                loadingMutableLiveData.value = false
                uiStateMutableLiveData.value = UiState.MoveToNextPageState
            }
        compositeDisposable.add(disposable)
    }

    fun observeInputTextChange(charSequence: String, missingInput: String?) {
        if (charSequence.isNotEmpty()){
            uiStateMutableLiveData.value = UiState.EnableButtonState(charSequence == missingInput)
        } else  uiStateMutableLiveData.value = UiState.EnableButtonState(false)
    }

    sealed class UiState {
        data class EnableButtonState(val enable: Boolean): UiState()
        object MoveToNextPageState: UiState()
    }

    override fun onCleared() {
        super.onCleared()
        clearDisposable()
    }

    private fun clearDisposable() {
        compositeDisposable.clear()
    }
}