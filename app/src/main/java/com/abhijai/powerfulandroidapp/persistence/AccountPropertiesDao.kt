package com.abhijai.powerfulandroidapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abhijai.powerfulandroidapp.models.AccountPropertiesModel

@Dao
interface AccountPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(accountPropertiesModel: AccountPropertiesModel) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountPropertiesModel: AccountPropertiesModel):Long

    @Query("Select * From account_properties Where pk = :pk")
    fun searchAccountPropertiesUsingPk(pk:Int) : LiveData<AccountPropertiesModel>

    @Query("Select * From account_properties Where email = :email")
    fun searchAccountPropertiesUsingEmail(email:String) : AccountPropertiesModel?

    @Query("Update account_properties Set email=:email, username =:username Where pk=:pk")
    fun updateAccountProperties(pk:Int, email:String, username:String)
}