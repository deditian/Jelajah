package com.tian.jelajah.ui.quran

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tian.jelajah.repositories.CommonRepository
import kotlinx.coroutines.launch

class QuranViewModel : ViewModel() {

    private val repository = CommonRepository()

    private var _surah = MutableLiveData<Unit>()

    val responseSurah = Transformations.switchMap(_surah) { repository.getListSurah() }

    fun _surah() = viewModelScope.launch {
        _surah.value = Unit
    }
}