package com.abhijai.powerfulandroidapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.abhijai.powerfulandroidapp.AppConstants
import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.persistence.AccountPropertiesDao
import com.abhijai.powerfulandroidapp.persistence.AppDatabase
import com.abhijai.powerfulandroidapp.persistence.AppDatabase.Companion.DATABASE_NAME
import com.abhijai.powerfulandroidapp.persistence.AuthTokenDao
import com.abhijai.powerfulandroidapp.util.LiveDataCallAdapterFactory
import com.abhijai.powerfulandroidapp.util.PreferenceKeys
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule{

    // excludeFieldsWithoutExposeAnnotation(b'coz of this retrofit is not going to read and item in our model or entities
    // which does not has @Expose annotation)
    @Singleton
    @Provides
    fun providesGsonBuilder():Gson{
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Singleton
    @Provides
    fun providesRetrofitBuilder(gson: Gson):Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() // get correct db version if schema changed
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthTokenDao(db: AppDatabase): AuthTokenDao {
        return db.getAuthTokenDao()
    }

    @Singleton
    @Provides
    fun provideAccountPropertiesDao(db: AppDatabase): AccountPropertiesDao {
        return db.getAccountPropertiesDao()
    }

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .placeholderOf(R.drawable.default_image)
            .error(R.drawable.default_image)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(application: Application, requestOptions: RequestOptions): RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application) : SharedPreferences{
        return application.getSharedPreferences(PreferenceKeys.APP_PREFERENCES,Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSharedPreferenceEditor(sharedPreferences: SharedPreferences) : SharedPreferences.Editor{
        return sharedPreferences.edit()
    }

}