package com.changui.mimochallengeandroid.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LessonContentConverter {
    companion object {
        private val gson = Gson().newBuilder().create()

        @TypeConverter
        @JvmStatic
        fun fromLessonContents(data: List<Content>): String {
            return if (data.isNullOrEmpty()) "" else gson.toJson(data)
        }

        @TypeConverter
        @JvmStatic
        fun toLessonContents(data: String): List<Content> {
            return if (data.isEmpty())
                emptyList()
            else {
                val listType = object : TypeToken<List<Content>>() {}.type
                gson.fromJson(data, listType)
            }
        }
    }
}