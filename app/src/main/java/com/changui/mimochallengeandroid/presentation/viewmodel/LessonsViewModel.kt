package com.changui.mimochallengeandroid.presentation.viewmodel

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changui.mimochallengeandroid.data.device.ReceiverLiveData
import com.changui.mimochallengeandroid.data.scheduler.RxSchedulersImpl
import com.changui.mimochallengeandroid.domain.Failure
import com.changui.mimochallengeandroid.domain.ResultState
import com.changui.mimochallengeandroid.domain.entity.LessonContent
import com.changui.mimochallengeandroid.domain.usecase.GetLessonsUseCase
import com.changui.mimochallengeandroid.domain.usecase.RestartGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class LessonsViewModel@Inject constructor (
    @ApplicationContext appContext: Context,
    private val getLessonsUseCase: GetLessonsUseCase,
                                           private val restartGameUseCase: RestartGameUseCase,
                                           private val rxSchedulers: RxSchedulersImpl): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var disposable: Disposable

    private val modelMutableLiveData: MutableLiveData<List<LessonContent>> = MutableLiveData<List<LessonContent>>()
    fun getActiveLessonsLiveData(): LiveData<List<LessonContent>> = modelMutableLiveData

    private val loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun getLoadingLiveData(): LiveData<Boolean> = loadingMutableLiveData

    private val errorMutableLiveData: MutableLiveData<Failure> = MutableLiveData<Failure>()
    fun getErrorLiveData(): LiveData<Failure> = errorMutableLiveData

    private val _activeNetworkInfoLiveData: MutableLiveData<Boolean> =
        ReceiverLiveData(appContext, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) {
                context: Context, _: Intent? ->
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            (cm).activeNetworkInfo?.isConnected == true
        }

    fun getInternetAccessLiveData(): LiveData<Boolean> = _activeNetworkInfoLiveData

    fun loadActiveLessons() {
        loadingMutableLiveData.value = true
        disposable = getLessonsUseCase.getAllLessons()
            .observeOn(rxSchedulers.main).subscribeBy {
                resultState ->
                when(resultState) {
                    is ResultState.Error -> {
                        errorMutableLiveData.value = resultState.error
                        loadingMutableLiveData.value = false
                    }
                    is ResultState.Success -> {
                        loadingMutableLiveData.value = false

                        val items = resultState.data
                        if (items == null)
                            errorMutableLiveData.value = Failure.UnknownError
                        else modelMutableLiveData.value = items
                    }
                }
            }

        compositeDisposable.add(disposable)
    }

    fun clearAllSolvedLessons() {
        disposable = restartGameUseCase.clearCacheForCompletedLessons()
            .observeOn(rxSchedulers.main).subscribeBy {
                loadActiveLessons()
            }

        compositeDisposable.add(disposable)
    }

    private fun clearDisposable() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        clearDisposable()
    }
}