package com.changui.mimochallengeandroid.presentation.view

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.changui.mimochallengeandroid.R
import com.changui.mimochallengeandroid.databinding.ActiveLessonsFragmentBinding
import com.changui.mimochallengeandroid.domain.entity.LessonContent
import com.changui.mimochallengeandroid.presentation.viewmodel.ActiveLessonViewModel
import com.changui.mimochallengeandroid.presentation.LessonLineMapper
import com.jakewharton.rxbinding.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ActiveLessonsFragment : Fragment(R.layout.active_lessons_fragment) {

    private var lessonContent: LessonContent? = null
    private var binding: ActiveLessonsFragmentBinding? = null
    private val activeLessonViewModel: ActiveLessonViewModel by viewModels()
    private val lessonParser = LessonLineMapper()
    private var listener: SolveCurrentLessonListener? = null
    private var missingInput: String? = null
    companion object {
        private const val EXTRA_CONTENT = "EXTRA_CONTENT"
        fun newInstance(lesson: LessonContent): ActiveLessonsFragment {
            val fragment = ActiveLessonsFragment()
            fragment.arguments = Bundle().also {
                it.putParcelable(EXTRA_CONTENT, lesson)
            }
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is SolveCurrentLessonListener)
            listener = activity as SolveCurrentLessonListener
        else throw RuntimeException(
            "SolveCurrentLessonListener not implemented by " + getContext()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            lessonContent = arguments?.getParcelable(EXTRA_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActiveLessonsFragmentBinding.bind(view)
        renderLessonContent()
        activeLessonViewModel.getUiStateLiveData().observe(viewLifecycleOwner, { uiState: ActiveLessonViewModel.UiState -> renderStateChange(uiState)} )
        activeLessonViewModel.getLoadingLiveData().observe(viewLifecycleOwner, {loadingState: Boolean -> showLoadingState(loadingState)})
        binding?.solveLessonBtn?.setOnClickListener {
            activeLessonViewModel.solveLesson(lessonContent!!.getLessonId(), Date().time.toString())
        }
    }

    private fun renderStateChange(uiState: ActiveLessonViewModel.UiState) {
        when(uiState) {
            is ActiveLessonViewModel.UiState.EnableButtonState -> enableOrDisableButton(uiState.enable)
            is ActiveLessonViewModel.UiState.MoveToNextPageState -> moveToNextLesson()
        }
    }

    private fun showLoadingState(loading: Boolean) {
        binding?.lessonStatusProgress?.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun moveToNextLesson() {
        Toast.makeText(activity, "Lesson completed", Toast.LENGTH_SHORT).show()
        binding?.solvedLesson?.visibility = View.VISIBLE
        listener?.moveToLesson()
    }

    private fun enableOrDisableButton(enable: Boolean) {
        if (enable) {
            binding?.solveLessonBtn?.alpha = 1f
            binding?.solveLessonBtn?.isEnabled = true
            binding?.solveLessonBtn?.isClickable = true
        } else {
            binding?.solveLessonBtn?.alpha = 0.5f
            binding?.solveLessonBtn?.isEnabled = false
            binding?.solveLessonBtn?.isClickable = false
        }
    }

    private fun renderLessonContent() {
        val lessonLines = lessonParser.mapToUI(lessonContent!!)
        lessonLines.forEach {
            if (it.spannableStringBuilder == null) {
                // content is of input type
                    enableOrDisableButton(false)
                    missingInput = it.missingInput
                val editText = EditText(activity).apply {
                    layoutParams = it.layoutParams
                    setTextSize(it.inputTextSize?.first!!, it.inputTextSize?.second!!)
                    setPadding(
                        it.paddingOptions!![0],
                        it.paddingOptions!![1],
                        it.paddingOptions!![2],
                        it.paddingOptions!![3]
                    )
                    setBackgroundResource(it.backResDrawable!!)
                    filters = arrayOf(InputFilter.LengthFilter(it.inputMaxLength!!))            // text max length
                    setTextColor(it.inputTextColor!!)
                    maxLines = 1
                    hint = "Type $missingInput"
                    requestFocus()
                }
                RxTextView.textChanges(editText).subscribe { charSequence ->
                    activeLessonViewModel.observeInputTextChange(editText.text.toString(), missingInput)
                }
                binding?.editableContent?.addView(editText)
            } else {
                // content if of read-only i.e text
                val textView = TextView(activity).apply {
                    layoutParams = it.layoutParams
                    setPadding(
                        it.paddingOptions!![0],
                        it.paddingOptions!![1],
                        it.paddingOptions!![2],
                        it.paddingOptions!![3]
                    )
                    setTextSize(it.inputTextSize?.first!!, it.inputTextSize?.second!!)
                    text = it.spannableStringBuilder
                }

                binding?.editableContent?.addView(textView)
            }
        }
    }

    private fun LessonContent.getLessonId(): Int {
        return when(this){
            is LessonContent.EditableContent -> this.id
            is LessonContent.NonEditableContent -> this.id
        }
    }

    interface SolveCurrentLessonListener {
        fun moveToLesson()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}