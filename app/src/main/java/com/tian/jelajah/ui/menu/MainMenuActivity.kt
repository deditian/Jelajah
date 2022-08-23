package com.tian.jelajah.ui.menu

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.tian.jelajah.R
import com.tian.jelajah.base.BaseActivty
import com.tian.jelajah.data.pref.Preferences
import com.tian.jelajah.databinding.ActivityMainMenuBinding
import com.tian.jelajah.model.Menus
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.receiver.ReminderReceiver
import com.tian.jelajah.repositories.ApiResponse
import com.tian.jelajah.services.ServiceHelper
import com.tian.jelajah.ui.quran.QuranActivity
import com.tian.jelajah.utils.*
import com.tian.jelajah.utils.Constants.BERITA
import com.tian.jelajah.utils.Constants.DOA
import com.tian.jelajah.utils.Constants.PAHLAWAN
import com.tian.jelajah.utils.Constants.PESANTREN
import com.tian.jelajah.utils.Constants.PUASA
import com.tian.jelajah.utils.Constants.QURAN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainMenuActivity : BaseActivty() {
    private val binding : ActivityMainMenuBinding by viewBinding()
    private val viewModel: MainMenuViewModel by viewModels()
    private val TAG = this::class.java.simpleName
    private var countDownTimer: CountDownTimer? = null
    private var prayers: List<Prayer>? = null
    private val preference: Preferences by lazy { Preferences(this) }

    override fun initData(savedInstanceState: Bundle?) {
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.my_color)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        ServiceHelper.runWorker(this)
        onView()
        setItemMenu()
    }

    private fun onView() = binding.run {
        rvMenus.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@MainMenuActivity.adapter
        }

        btnNotification.setOnClickListener {
            val item = prayers?.find { prayer -> prayer.time.isValid() }
            if (item != null) {
                preference.notifications.let { list ->
                    preference.notifications = if (item.alarm)  list.filter { f -> f != item.name }
                    else ArrayList(list).apply { add(item.name) }
                }
                if (item.alarm) {
                    binding.btnNotification.setImageResource(if (item.type == "notify")
                        R.drawable.ic_notifications_off else R.drawable.ic_baseline_volume_off_24)
                } else {
                    binding.btnNotification.setImageResource(if (item.type == "notify")
                        R.drawable.ic_notifications else R.drawable.ic_baseline_volume_up_24)
                }
                ReminderReceiver.updateAlarm(this@MainMenuActivity)
                viewModel.prayers()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.prayers()
        viewModel._jadwalSholat(preference.locationLatLongi!!)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        viewModel.prayers()
        viewModel.responsePrayers.observe(this) {
            it?.let { list ->
                prayers = list.run {
                    val prayers = ArrayList<Prayer>()
                    forEach { prayer ->  prayers.add(prayer.copy(alarm = preference.notifications.find { notify -> notify == prayer.name } != null)) }
                    return@run prayers
                }
                updatePrayer(prayers!!)
            }
        }

        viewModel.responseJadwalSholat.observe(this){
            when(it) {
                is ApiResponse.Error -> {
                    hideProgressDialog()
                    Log.e(TAG, "onCreate error: ${it.error}" )
                }
                ApiResponse.Loading -> {
                    showProgressDialog()
                    binding.txtCurrentLocation.text = getString(R.string.loading)
                }
                is ApiResponse.Success -> {
                    hideProgressDialog()
                    prayers = it.data.run {
                        val prayers = ArrayList<Prayer>()
                        forEach { prayer ->  prayers.add(prayer) }
                        return@run prayers
                    }
                    updatePrayer(it.data)
                    binding.txtCurrentLocation.text = preference.city
                }
            }
        }

    }

    private fun updatePrayer(prayer : List<Prayer>) {
        prayer.forEach {
            if (it.time.isValid()) {
                Log.e(TAG, "updatePrayer: $it" )
                val string = "${getStringWithNameId(it.name)} ${dateFormat("HH:mm", it.time)}"
                CoroutineScope(Dispatchers.Main).launch {
                    binding.txtTimePrayer.text = string
                    binding.txtDate.text = dateFormatParse("EEEE, dd MMMM yyyy",it.date)
                }
                countDownTimer?.cancel()
                countDownTimer = null
                runCountDown(it.time)
                return
            }
        }

        Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DAY_OF_MONTH, 1)
            viewModel.prayers(dateFormat(time =  time.time))
        }
    }

    private fun runCountDown(time: Long?) {
        val countTimeLong: Long
        val dNow = Date()
        if (time == null) return else {
            if (time <= dNow.time) return
            countTimeLong = time - dNow.time
        }

        if (countDownTimer != null) return

        countDownTimer = object : CountDownTimer(countTimeLong, 1000L) {
            override fun onTick(elapsedTime: Long) {
                binding.txtCountdownPrayer.text = createTimeString(elapsedTime)
            }

            override fun onFinish() {
                nowTimePrayer {
                    updatePrayer(prayers!!)
                }
            }
        }
        countDownTimer?.start()
    }

    private fun nowTimePrayer(callbackFinish: () -> Unit) {
        binding.materialTextView2.setText(R.string.now_time_prayer)
        val colorTo = binding.txtCountdownPrayer.currentTextColor
        ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, colorTo).apply {
            duration = 500L
            repeatCount = 10000 / duration.toInt()
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                if (it.animatedValue is Int) binding.txtCountdownPrayer.setTextColor(it.animatedValue as Int)
            }
            this.doOnEnd {
                callbackFinish.invoke()
                binding.materialTextView2.setText(R.string.next_time_prayer)
            }
            start()
        }
    }

    private fun setItemMenu() {
        val array = ArrayList<Menus>()
        array.add(Menus(Color.WHITE, QURAN))
        array.add(Menus(Color.WHITE, DOA))
        array.add(Menus(Color.WHITE, PUASA))
        array.add(Menus(Color.WHITE, BERITA))
        array.add(Menus(Color.WHITE, PESANTREN))
        array.add(Menus(Color.WHITE, PAHLAWAN))
        adapter.submitList(array)
    }

    private val adapter = MainMenuAdapter().apply {
        listener = object : MainMenuAdapter.RecyclerViewClickListener {
            override fun onItemClicked(view: View, item: Menus) {

                when (item.name){
                    QURAN ->{
                        gotoActivity(QuranActivity::class)
                    }
                    DOA ->{

                    }
                    PUASA ->{

                    }
                    BERITA ->{

                    }
                    PESANTREN ->{

                    }
                    PAHLAWAN ->{

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}