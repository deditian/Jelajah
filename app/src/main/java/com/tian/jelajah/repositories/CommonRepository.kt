package com.tian.jelajah.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tian.jelajah.di.AppModule
import com.tian.jelajah.model.*
import com.tian.jelajah.utils.convertToList
import com.tian.jelajah.utils.dateFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.reflect.KClass


class CommonRepository {

    private val apiService = AppModule.getInstance()

    fun getJadwalSholat(latAndLong : String) : LiveData<ApiResponse<List<Prayer>>> {
        val result = MutableLiveData<ApiResponse<List<Prayer>>>()

        result.value = ApiResponse.Loading
        val dates = dateFormat("yyyy-MM-dd").split("-")
        val latlong = latAndLong.split("|")
        val lat = latlong[0].toDouble()
        val longi = latlong[1].toDouble()

        apiService.provideApiJadwalService().getJadwalSholat(lat, longi,
            dates[0].toInt(), dates[1].toInt(),
            dates[2].toInt()).enqueue(enqueue(JadwalSholatResponse::class, { it ->
            try {
                val list = ArrayList<Prayer>()
                it.let {
                    it.data.forEach { jadwal ->
                        list.addAll(jadwal.convertToList())
                    }
                }
                val prayers = list.filter { it.date == dates.joinToString("-") }
                Log.e("TAG", "getJadwal: ${prayers}" )
                result.value = ApiResponse.Success(prayers)
            } catch (e: Exception) {
                result.value = ApiResponse.Error(it)
            }


        }, {
            result.value = ApiResponse.Error(it)
        }))

        return result
    }

    fun getListSurah() : LiveData<ApiResponse<List<Surah>>> {
        val result = MutableLiveData<ApiResponse<List<Surah>>>()

        result.value = ApiResponse.Loading

        apiService.provideApiQuranService().getListSurah().enqueue(enqueue(SurahListResponse::class, {
            Log.e("TAG", "getListSurah: $it" )
            result.value = ApiResponse.Success(it.data)
        }, {
            result.value = ApiResponse.Error(it)
        }))

        return result
    }

    private fun <T : GeneralResponse> enqueue(clazz: KClass<T>, callSuccess: (response: T) -> Unit, callError: (error: GeneralResponse) -> Unit) = object : Callback<String?> {
        override fun onResponse(call: Call<String?>, response: Response<String?>) {
            if (response.isSuccessful) {
                Log.e("TAG", "onResponse response.body:: ${response.body()}" )
                val generalResponse = Gson().fromJson(response.body(), GeneralResponse::class.java)
                val responseStatus = generalResponse.status
                if ("success".equals(responseStatus, ignoreCase = true) || "200".equals(responseStatus, ignoreCase = true)) {
                    val r : T = Gson().fromJson(response.body(), clazz.java)
                    Log.e("Repo", "$r")
                    callSuccess.invoke(r)
                } else {
                    callError(generalResponse)
                }
            } else {
                val error = buildError(response.message(), "0" + response.code())
                callError.invoke(error)
            }
        }

        override fun onFailure(call: Call<String?>, t: Throwable) {
            val error = getErrorResponse(t.message)
            callError.invoke(error)
        }
    }

    private fun getErrorResponse(message: String?): GeneralResponse {
        var msg: String? = message
        if (msg != null && msg.lowercase(Locale.getDefault()).contains("timeout")) {
            msg = "Service Timeout. Check jaringan internet atau hubungi teknisi"
        } else if (msg != null && msg.lowercase(Locale.getDefault()).contains("failed")) {
            msg = "Koneksi gagal. Check jaringan internet atau hubungi teknisi"
        }
        return buildError(msg, null)
    }

    private fun buildError(message: String?, code: String?): GeneralResponse {
        var msg: String? = message
        if (msg != null && msg.isEmpty()) {
            msg = "Koneksi gagal. Check jaringan internet atau hubungi teknisi"
        }
        if (code != null && code.isNotEmpty() && code == "0401") {
            msg = "Session expired. Please re-login!"
        }
        return GeneralResponse().apply {
            this.responseCode = if (code != null && code.isNotEmpty()) code else "XX"
            this.message = msg?:""
        }
    }
}