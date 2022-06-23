package com.tian.jelajah.ui.quran

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.tian.jelajah.R
import com.tian.jelajah.repositories.ApiResponse

class QuranFragment : Fragment() {

    private val viewModel: QuranViewModel by activityViewModels()
    private val TAG = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel._surah()
        viewModel.responseSurah.observe(viewLifecycleOwner){
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_surah, container, false)
    }

}