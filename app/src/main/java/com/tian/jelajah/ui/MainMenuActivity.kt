package com.tian.jelajah.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tian.jelajah.R
import com.tian.jelajah.databinding.ActivityMainMenuBinding
import com.tian.jelajah.repositories.ApiResponse
import com.tian.jelajah.ui.surah.SurahViewModel
import com.tian.jelajah.utils.obtainViewModel

class MainMenuActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainMenuBinding
    private val viewModel by obtainViewModel(SurahViewModel::class)
    private val TAG = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel._surah()

        viewModel.responseSurah.observe(this){
            when(it) {
                is ApiResponse.Error -> {
                    Log.e(TAG, "onCreate error: ${it.error}" )
                }
                ApiResponse.Loading -> {

                }
                is ApiResponse.Success -> {
                    Log.e(TAG, "onCreate Success:  ${it.data}" )
                }
            }
        }
    }
}