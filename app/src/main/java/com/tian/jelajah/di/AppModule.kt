package com.tian.jelajah.di



import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.tian.jelajah.BuildConfig
import com.tian.jelajah.data.api.ApiServices
import com.tian.jelajah.utils.Constants
import com.tian.jelajah.utils.Pref
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class AppModule {
    companion object {

        private var appModule: AppModule? = null

        @JvmStatic
        fun getInstance() : AppModule {
            if (appModule == null) appModule = AppModule()

            return appModule!!
        }
    }

    fun provideApiJadwalService(): ApiServices {
        return buildClient(BuildConfig.BASE_URL_JADWAL).create(ApiServices::class.java)
    }

    fun provideApiQuranService(): ApiServices {
        return buildClient(BuildConfig.BASE_URL_QURAN).create(ApiServices::class.java)
    }

    private fun buildClient(baseUrl: String): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkHttpClient())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(getHttpInterceptor())
            .build()
    }

    private fun getHttpInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val originalHttpUrl = original.url
            val currentLanguage: String = Pref.getString(Constants.LANGUAGE)
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("language", currentLanguage)
                .build()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder().url(url)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }
    }
}