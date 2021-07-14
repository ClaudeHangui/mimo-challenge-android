package com.changui.mimochallengeandroid

import android.content.Context
import androidx.room.Room
import com.changui.mimochallengeandroid.data.*
import com.changui.mimochallengeandroid.data.local.ActiveLessonsLocalDataStoreImpl
import com.changui.mimochallengeandroid.data.local.CompletedLessonLocalDataStoreImpl
import com.changui.mimochallengeandroid.data.local.LessonsDao
import com.changui.mimochallengeandroid.data.local.MimoDatabase
import com.changui.mimochallengeandroid.data.remote.GetLessonsErrorFactory
import com.changui.mimochallengeandroid.data.remote.GetLessonsRemoteDataStoreImpl
import com.changui.mimochallengeandroid.data.remote.MimoApiService
import com.changui.mimochallengeandroid.data.scheduler.RxSchedulersImpl
import com.changui.mimochallengeandroid.domain.repository.LessonsRepository
import com.changui.mimochallengeandroid.domain.usecase.*
import com.changui.mimochallengeandroid.presentation.LessonLineMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL = "https://mimochallenge.azurewebsites.net/"
const val DB_NAME = "mimo.db"

@InstallIn(SingletonComponent::class)
@Module(includes = [AbstractProvision::class])
class NonStaticProvision {
    @Provides
    @Singleton
    fun provideLessonsDao(appDatabase: MimoDatabase): LessonsDao {
        return appDatabase.lessonsDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): MimoDatabase {
        return Room.databaseBuilder(
            appContext,
            MimoDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideApiInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            val url = request.url.newBuilder().build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(apiInterceptor: Interceptor, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(apiInterceptor)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): MimoApiService {
        return retrofit.create(MimoApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideErrorFactory(): GetLessonsErrorFactory {
        return GetLessonsErrorFactory()
    }

    @Singleton
    @Provides
    fun provideMapper(): RemoteToLocalLessonsMapper {
        return RemoteToLocalLessonsMapper()
    }

    @Singleton
    @Provides
    fun provideUILessonMapper(): LessonLineMapper {
        return LessonLineMapper()
    }

    @Singleton
    @Provides
    fun provideScheduler(): RxSchedulersImpl {
        return RxSchedulersImpl()
    }
}

@InstallIn(SingletonComponent::class)
@Module
abstract class AbstractProvision {
    @Singleton
    @Binds
    abstract fun bindGetLessonsRemoteDataStore(remoteDataStoreImpl: GetLessonsRemoteDataStoreImpl): GetLessonsRemoteDataStore

    @Singleton
    @Binds
    abstract fun bindActiveLessonsLocalDataStore(activeLessonsLocalDataStoreImpl: ActiveLessonsLocalDataStoreImpl): ActiveLessonsLocalDataStore

    @Singleton
    @Binds
    abstract fun bindCompletedLessonsLocalDataStore(completedLessonLocalDataStoreImpl: CompletedLessonLocalDataStoreImpl): CompletedLessonLocalDataStore

    @Singleton
    @Binds
    abstract fun bindLessonRepository(lessonRepositoryImpl: LessonRepositoryImpl) : LessonsRepository

    @Singleton
    @Binds
    abstract fun bindGetLessonsUseCase(getLessonsUseCase: GetLessonsUseCaseImpl): GetLessonsUseCase

    @Singleton
    @Binds
    abstract fun bindCompleteLessonUseCase(completeLessonUseCase: CompleteLessonUseCaseImpl): CompleteLessonUseCase

    @Singleton
    @Binds
    abstract fun bindRestartUseCase(restartGameUseCase: RestartGameUseCaseImpl): RestartGameUseCase
}
