package com.tian.jelajah.repositories


sealed class LocalResponse<out R> {
    data class Success<out T>(val data: T) : LocalResponse<T>()
    data class Error(val error: String?) : LocalResponse<Nothing>()
    object Loading : LocalResponse<Nothing>()
}
