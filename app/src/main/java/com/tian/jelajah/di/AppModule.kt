package com.tian.jelajah.di



import android.app.Application
import android.content.Context
import androidx.preference.Preference
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.tian.jelajah.BuildConfig
import com.tian.jelajah.data.api.JadwalServices
import com.tian.jelajah.data.api.QuranServices
import com.tian.jelajah.data.db.AppDatabase
import com.tian.jelajah.data.db.AppDatabase.Companion.DATABASE_NAME
import com.tian.jelajah.data.pref.Preferences
import com.tian.jelajah.utils.Constants
import com.tian.jelajah.utils.Pref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideApiJadwalService(): JadwalServices {
        return buildClient(BuildConfig.BASE_URL_JADWAL).create(JadwalServices::class.java)
    }

    @Provides
    @Singleton
    fun provideApiQuranService(): QuranServices {
        return buildClient(BuildConfig.BASE_URL_QURAN).create(QuranServices::class.java)
    }

    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideAppDao(db: AppDatabase) = db.prayerDao()

    @Provides
    @Singleton
    fun providePreferences(app: Application): Preferences {
        return Preferences(app)
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