package com.tian.jelajah.ui.quran

import android.app.Application
import androidx.lifecycle.*
import com.tian.jelajah.repositories.CommonRepository
import kotlinx.coroutines.launch

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CommonRepository(application)

    private var _surah = MutableLiveData<Unit>()

    val responseSurah = Transformations.switchMap(_surah) { repository.getListSurah() }

    fun _surah() = viewModelScope.launch {
        _surah.value = Unit
    }
}