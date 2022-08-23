package com.tian.jelajah.ui.adzan

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.View
import android.view.WindowManager
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.tian.jelajah.data.pref.Preferences
import com.tian.jelajah.databinding.FragmentDialogAdzanBinding
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.ui.menu.MainMenuViewModel
import java.lang.IllegalStateException


class AdzanDialogFragment(private val time: Long, private val callbackDismiss: (() -> Unit?)? = null) : DialogFragment(), MediaPlayer.OnCompletionListener {


    lateinit var preference: Preferences
    private var mMediaPlayer: MediaPlayer? = null
    private var mAudioManager: AudioManager? = null
    private var prayer: Prayer? = null

    private var mOriginalVolume = -1
    private var mAudioStream = AudioManager.STREAM_ALARM
    val binding : FragmentDialogAdzanBinding by viewBinding()
    private val viewModel: MainMenuViewModel by activityViewModels()

    private val mOnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            stopAlarm()
            dismiss()
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            mMediaPlayer?.start()
        }
    }
    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                mOnAudioFocusChangeListener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().volumeControlStream = AudioManager.STREAM_ALARM
        viewModel.prayers()

//        binding.close.setOnClickListener {
//            stopAlarm()
//            dismiss()
//        }
//
//        viewModel.responsePrayers.observe(this) {
//            it.find { prayer -> prayer.time == time }?.let { prayer -> setPray(prayer) }
//        }
    }

//    private fun setPray(prayer: Prayer) {
//        this.prayer = prayer
//        binding.prayerName.text = getString(R.string.time_now_shalat, getString(prayer.name.nameResource(R.string::class.java)))
//        Binding.dateTimeFormat(binding.prayerTime, prayer.time)
//        uiBackground(prayer.backgroundColor, isNight())
//        try {
//            playAlarm(prayer.name)
//        } catch (e: Exception) {
//            Log.e("RingAlarmActivity", e.message, e)
//        }
//        val telephonyManager = requireActivity().getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
//    }

    @Throws(Exception::class)
//    private fun playAlarm(adzan: String) {
//        val assetFileDescriptor = if (adzan == "fajr") {
////            resources.openRawResourceFd(R.raw.fajr)
//        } else {
////            resources.openRawResourceFd(R.raw.normal)
//        }
//        mMediaPlayer = MediaPlayer().apply {
//            setDataSource(assetFileDescriptor!!.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
//            prepare()
//            setOnCompletionListener(this@AdzanDialogFragment)
//        }
//        mAudioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        mAudioManager?.apply {
//            mOriginalVolume = getStreamVolume(mAudioStream)
//            if (mOriginalVolume == 0) {
//                val volume: Int = ceil(getStreamMaxVolume(mAudioStream).toDouble() * (50f / 100.0)).toInt()
//               setStreamVolume(AudioManager.STREAM_ALARM, volume, 0)
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val result: Int = requestAudioFocus(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build())
//                if (result == AudioManager.AUDIOFOCUS_GAIN) {
//                    mMediaPlayer?.start()
//                }
//            }
//        }
//    }

    private fun stopAlarm() {
        try {
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
            mMediaPlayer = null
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        if (mOriginalVolume != -1) {
            mAudioManager?.setStreamVolume(mAudioStream, mOriginalVolume, 0)
        }
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //mAudioManager?.abandonAudioFocusRequest(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_LOSS).build())
        //}
        val telephonyManager = requireActivity().getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        requireDialog().window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCompletion(mp: MediaPlayer) {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
        val assetFileDescriptor = when (prayer?.name) {
            "fajr" ->{}
//                resources.openRawResourceFd(R.raw.dua_sehri)
            "maghrib" ->{}
//                resources.openRawResourceFd(R.raw.dua_iftar)
            else -> null
        }

        if (assetFileDescriptor == null) {
            stopAlarm()
            return
        }

//        try {
//            mMediaPlayer = MediaPlayer().apply {
//                setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
//                setOnCompletionListener { stopAlarm() }
//                prepare()
//                GlobalScope.launch(Dispatchers.Default) {
//                    delay(3000)
//                    mMediaPlayer?.start()
//                }
//            }
//        } catch (e: java.lang.Exception) {
//            Log.e("RingAlarmActivity", e.message, e)
//        }
    }

    override fun dismiss() {
        callbackDismiss?.invoke()
        super.dismiss()
    }

//    private fun uiBackground(color: Int, isNight: Boolean) {
//        binding.background.backgroundTintList = ColorStateList.valueOf(color)
//        binding.imgTintMosque.imageTintList = ColorStateList.valueOf(color)
//
//        if (isNight) {
//            binding.imgLighting.setImageResource(R.drawable.ic_light)
//            binding.imgSunMoon.setImageResource(R.drawable.ic_moon)
//            binding.imgSunMoon.imageTintList = ColorStateList.valueOf(Color.WHITE)
//            binding.imgLighting.imageTintList = ColorStateList.valueOf(Color.WHITE)
//        } else {
//            binding.imgLighting.setImageResource(R.drawable.ic_light2)
//            binding.imgSunMoon.setImageResource(R.drawable.ic_sun)
//            binding.imgSunMoon.imageTintList = ColorStateList.valueOf(Color.parseColor("#eeee8e"))
//            binding.imgLighting.imageTintList = ColorStateList.valueOf(Color.parseColor("#eeee8e"))
//        }
//    }
}