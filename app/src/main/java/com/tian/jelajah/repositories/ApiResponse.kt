package com.tian.jelajah.repositories

import com.tian.jelajah.model.GeneralResponse


sealed class ApiResponse<out R> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val error: GeneralResponse) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}
