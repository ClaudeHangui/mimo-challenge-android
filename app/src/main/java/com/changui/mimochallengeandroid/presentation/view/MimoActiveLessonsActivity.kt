package com.changui.mimochallengeandroid.presentation.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.changui.mimochallengeandroid.R
import com.changui.mimochallengeandroid.databinding.MimoActiveLessonsActivityBinding
import com.changui.mimochallengeandroid.domain.Failure
import com.changui.mimochallengeandroid.domain.entity.LessonContent
import com.changui.mimochallengeandroid.presentation.viewmodel.LessonsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MimoActiveLessonsActivity : AppCompatActivity(R.layout.mimo_active_lessons_activity),
    ActiveLessonsFragment.SolveCurrentLessonListener {

    private val viewModel: LessonsViewModel by viewModels()

    private var binding: MimoActiveLessonsActivityBinding? = null
    private var mCurrentVisiblePosition = -1
    private var mLastLessonPosition = 0
    private lateinit var pagerAdapter: LessonPagerAdapter
    private var alertDialog: AlertDialog ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MimoActiveLessonsActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel.getInternetAccessLiveData().observe(this, { isOnline: Boolean -> checkInternetState(isOnline)})

        binding?.pager?.isUserInputEnabled = false
        binding?.restartGame?.setOnClickListener { restartGame() }
        binding?.exitGame?.setOnClickListener {
            exitApp()
        }

        viewModel.loadActiveLessons()
        viewModel.getLoadingLiveData().observe(this, { loadingState: Boolean ->
            renderLoaderState(loadingState)
        })
        viewModel.getErrorLiveData().observe(this, { failureState: Failure ->
            renderFailureState(failureState)
        })
        viewModel.getActiveLessonsLiveData().observe(this, { items: List<LessonContent> ->
            showLessonsState(items)
        })
    }

    private fun checkInternetState(isOnline: Boolean) {
        if (isOnline){
            if (binding?.failureGroup?.visibility == View.VISIBLE) {
                binding?.failureGroup?.visibility = View.GONE
                viewModel.loadActiveLessons()
            } else if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()
        } else {
            showSettingsDialog()
        }
    }

    private fun restartGame() {
        binding?.group?.visibility = View.GONE
        viewModel.clearAllSolvedLessons()
    }

    private fun showSettingsDialog() {
        if( alertDialog != null && alertDialog!!.isShowing) return

        val builder = AlertDialog.Builder(this)
        builder.setTitle("You are offline")
        builder.setMessage("You can activate your internet access by opening your phone settings page")
        builder.setNegativeButton(android.R.string.no, negativeButtonClick)
        builder.setPositiveButton(android.R.string.ok, onPositiveButtonClick)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog?.show()
    }

    private val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
    }

    private val onPositiveButtonClick = { dialog: DialogInterface, which: Int ->
        showSettings()
    }

    private fun exitApp() {
        if(Build.VERSION.SDK_INT in 16..20){
            finishAffinity()
        } else if(Build.VERSION.SDK_INT >= 21){
            finishAndRemoveTask()
        }
    }

    private fun renderFailureState(failureState: Failure) {
        val failureDesc: String = when(failureState){
            is Failure.NetworkError -> getString(R.string.error_network_desc)
            is Failure.ServerError -> getString(R.string.error_internal_server_desc)
            is Failure.BadRequestError -> getString(R.string.error_forbidden_desc)
            is Failure.GatewayError -> getString(R.string.error_gateway_desc)
            else -> getString(R.string.error_unknown_desc)
        }
        binding?.failureDescription?.text = failureDesc
        binding?.failureBtn?.setOnClickListener {
            showSettings()
        }
        binding?.failureGroup?.visibility = View.VISIBLE
    }

    private fun showSettings(){
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    private fun showLessonsState(items: List<LessonContent>) {
        if (items.isEmpty()) renderAllSolvedLessons()
        else renderActiveLessons(items)
    }

    private fun renderRestart() {
        finish()
        startActivity(Intent(this, MimoActiveLessonsActivity::class.java))
    }

    private fun renderLoaderState(showLoading: Boolean) {
        binding?.progress?.visibility = if (showLoading) View.VISIBLE else View.GONE
    }

    private fun renderAllSolvedLessons() {
        binding?.group?.visibility = View.VISIBLE
        binding?.restartGame?.setOnClickListener { viewModel.clearAllSolvedLessons() }
    }

    private fun renderActiveLessons(items: List<LessonContent>) {
        pagerAdapter = LessonPagerAdapter(supportFragmentManager, lifecycle, items)
        binding?.pager?.adapter = pagerAdapter
        binding?.group?.visibility = View.GONE
        binding?.pager?.visibility = View.VISIBLE
        mLastLessonPosition = pagerAdapter.itemCount - 1
    }

    override fun onDestroy() {
        binding = null
        if (alertDialog != null && alertDialog?.isShowing!!) {
            alertDialog!!.dismiss()
        }
        super.onDestroy()
    }

    inner class LessonPagerAdapter(
        fm: FragmentManager,
        lifecycle: Lifecycle,
        private val items: List<LessonContent>
    ): FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int {
            return if (items.isEmpty()) 0 else items.size
        }

        override fun createFragment(position: Int): Fragment {
            if (itemCount > 0) {
                mCurrentVisiblePosition = position
            }
            return ActiveLessonsFragment.newInstance(items[position])
        }
    }

    override fun moveToLesson() {
        if (mCurrentVisiblePosition > -1) {
            if (mCurrentVisiblePosition == mLastLessonPosition) renderRestart()
            else binding?.pager?.setCurrentItem(mCurrentVisiblePosition + 1, true)
        }
    }


}

