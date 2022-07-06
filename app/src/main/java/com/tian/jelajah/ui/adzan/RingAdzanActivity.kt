package com.tian.jelajah.ui.adzan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tian.jelajah.R
import com.tian.jelajah.receiver.ReminderReceiver.Companion.ACTION_REMINDER
import com.tian.jelajah.utils.Constants.EXTRA_PRAYER_NOW

class RingAdzanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring_adzan)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent)  {
        if (intent.action == ACTION_REMINDER) {
            Log.e("RingAdzanActivity", "handleIntent: ACTION_REMINDER", )
            intent.getLongExtra(EXTRA_PRAYER_NOW, 0L).let {
                Log.e("RingAdzanActivity", "handleIntent: EXTRA_PRAYER_NOW $it" )
                if (it > 0L) {
                    Log.e("RingAdzanActivity", "handleIntent: ACTION_REMINDER MASUK", )
                    Toast.makeText(this,"asdasdasdasdsadasdaa",Toast.LENGTH_SHORT).show()
                    with(AdzanDialogFragment(it) { finish() }) {
                        isCancelable = false
                        show(supportFragmentManager, "adzan_dialog")
                    }
                } else finish()
            }
        }
    }
}