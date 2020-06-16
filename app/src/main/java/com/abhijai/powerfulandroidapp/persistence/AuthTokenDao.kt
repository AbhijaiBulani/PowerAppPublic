package com.abhijai.powerfulandroidapp.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abhijai.powerfulandroidapp.models.AuthTokenModel

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authTokenModel: AuthTokenModel):Long // return's the Long which denote the row which get inserted

    @Query("Update auth_token Set token = null Where account_pk = :pk")
    fun nullifyToken(pk:Int):Int // return's Int which denote the node which get updated

    @Query("SELECT * FROM auth_token WHERE account_pk = :pk")
    suspend fun searchByPk(pk: Int): AuthTokenModel?
}