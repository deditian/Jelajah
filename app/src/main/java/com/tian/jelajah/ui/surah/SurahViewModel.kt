package com.tian.jelajah.ui.surah

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tian.jelajah.repositories.QuranRepository
import kotlinx.coroutines.launch

class SurahViewModel : ViewModel() {

    private val repository = QuranRepository()

    private var _surah = MutableLiveData<Unit>()

    val responseSurah = Transformations.switchMap(_surah) { repository.getListSurah() }

    fun _surah() = viewModelScope.launch {
        _surah.value = Unit
    }
}