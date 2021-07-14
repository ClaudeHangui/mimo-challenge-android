package com.changui.mimochallengeandroid.presentation

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.text.color
import com.changui.mimochallengeandroid.R
import com.changui.mimochallengeandroid.domain.Mapper
import com.changui.mimochallengeandroid.domain.entity.LessonContent

class LessonLineMapper: Mapper<LessonContent, List<LessonLine>> {

    override fun mapToUI(input: LessonContent): MutableList<LessonLine> {
        val lessonLines = mutableListOf<LessonLine>()
        when(input) {
            is LessonContent.NonEditableContent -> {
                val lessonLine = LessonLine()
                val stringBuilder = SpannableStringBuilder()
                lessonLine.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                lessonLine.paddingOptions = listOf(8, 0, 8, 0)
                lessonLine.inputTextSize = Pair(TypedValue.COMPLEX_UNIT_SP,18f)
                input.content.forEach {
                    val contentColor = Color.parseColor(it.color)
                    stringBuilder.color(contentColor) { append(it.text) }
                }
                lessonLine.spannableStringBuilder = stringBuilder
                lessonLines.add(lessonLine)
            }

            is LessonContent.EditableContent -> {
                var contentConcatenated = ""
                var contentItemStart = 1
                var isInputParsed = false

                input.content.forEach {
                    contentConcatenated += it.text
                    if (contentConcatenated.length >= input.startIndex && !isInputParsed){
                        if (contentItemStart < input.startIndex) {
                            val startingTextContent = contentConcatenated.substring(contentItemStart - 1, input.startIndex)
                            val startingTextColor = Color.parseColor(it.color)
                            val textLessonLine = LessonLine()
                            textLessonLine.inputTextSize = Pair(TypedValue.COMPLEX_UNIT_SP,18f)
                            val stringBuilder = SpannableStringBuilder()
                            textLessonLine.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            textLessonLine.paddingOptions = listOf(8, 0, 8, 0)
                            stringBuilder.color(startingTextColor) { append(startingTextContent) }
                            textLessonLine.spannableStringBuilder = stringBuilder
                            lessonLines.add(textLessonLine)
                        }

                        val endIndex = if(contentConcatenated.length >= input.endIndex) input.endIndex else
                            contentConcatenated.length

                        val inputText = contentConcatenated
                        val inputChunk = if (contentItemStart >= 1) contentConcatenated.substring(contentItemStart - 1, endIndex) else contentConcatenated.substring(
                            input.startIndex - 1, endIndex)

                        val inputLessonLine = LessonLine()
                        val inputTextMaxLength = if (contentItemStart < 1) contentConcatenated.substring(inputText.length, inputText.length + inputChunk.length).length else contentConcatenated.substring(0, inputChunk.length).length

                        inputLessonLine.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.1f).apply {
                            gravity = Gravity.CENTER_VERTICAL
                        }
                        inputLessonLine.inputTextSize = Pair(TypedValue.COMPLEX_UNIT_SP,18f)
                        inputLessonLine.paddingOptions = listOf(8, 8, 8, 8)
                        inputLessonLine.backResDrawable = R.drawable.rounded_corner_light_blue
                        inputLessonLine.inputMaxLength = inputTextMaxLength
                        inputLessonLine.inputTextColor = Color.parseColor(it.color)
                        inputLessonLine.missingInput = inputChunk

                        if (contentConcatenated.length >= input.endIndex){
                            lessonLines.add(inputLessonLine)

                            val endingTextLessonLine = LessonLine()
                            val endingTextContent = contentConcatenated.substring(input.endIndex, contentConcatenated.length)
                            endingTextLessonLine.inputTextSize = Pair(TypedValue.COMPLEX_UNIT_SP,18f)
                            val endingTextColor = Color.parseColor(it.color)
                            val endingStringBuilder = SpannableStringBuilder()
                            endingTextLessonLine.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            endingTextLessonLine.paddingOptions = listOf(10, 0, 8, 0)
                            endingStringBuilder.color(endingTextColor) { append(endingTextContent) }
                            endingTextLessonLine.spannableStringBuilder = endingStringBuilder
                            lessonLines.add(endingTextLessonLine)
                            isInputParsed = true
                        }

                    } else {
                        val textLessonLine = LessonLine()
                        val stringBuilder = SpannableStringBuilder()
                        textLessonLine.inputTextSize = Pair(TypedValue.COMPLEX_UNIT_SP,18f)
                        textLessonLine.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f)
                        textLessonLine.paddingOptions = listOf(0, 0, 8, 0)
                        stringBuilder.color(Color.parseColor(it.color)) { append(it.text) }
                        textLessonLine.spannableStringBuilder = stringBuilder
                        lessonLines.add(textLessonLine)
                    }
                    contentItemStart += it.text.length
                }
            }
        }
        return lessonLines
    }
}


data class LessonLine (var spannableStringBuilder: SpannableStringBuilder?= null,
                  var paddingOptions : List<Int> ?= emptyList(),
                  var layoutParams: LinearLayout.LayoutParams ?= null,
                  var inputTextSize: Pair<Int, Float> ?= null,
                  var inputMaxLength: Int ?= null,
                  @DrawableRes var backResDrawable: Int? = null,
                  @ColorRes var inputTextColor: Int? = null,
                  var missingInput: String ?= null
)

