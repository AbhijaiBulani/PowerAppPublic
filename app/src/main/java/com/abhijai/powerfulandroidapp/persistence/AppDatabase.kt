package com.abhijai.powerfulandroidapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhijai.powerfulandroidapp.AppConstants
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel
import com.abhijai.powerfulandroidapp.models.AuthTokenModel
import kotlin.reflect.KClass

//class<?>[]
@Database(entities = [
                        AccountPropertiesModel::class,
                        AuthTokenModel::class
                        ]
    ,version = 1)
abstract class AppDatabase:RoomDatabase() {

    abstract fun getAuthTokenDao() : AuthTokenDao

    abstract fun getAccountPropertiesDao() : AccountPropertiesDao

    companion object{
        const val DATABASE_NAME = "power_db";
    }

}