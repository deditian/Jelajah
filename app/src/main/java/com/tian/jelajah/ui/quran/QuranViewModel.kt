package com.tian.jelajah.ui.quran

import androidx.lifecycle.*
import com.tian.jelajah.repositories.CommonRepositoryImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuranViewModel @Inject constructor(val repositoryImpl : CommonRepositoryImpl) : ViewModel() {

    private var _surah = MutableLiveData<Unit>()

    val responseSurah = Transformations.switchMap(_surah) { repositoryImpl.getListSurah() }

    fun _surah() = viewModelScope.launch {
        _surah.value = Unit
    }
}