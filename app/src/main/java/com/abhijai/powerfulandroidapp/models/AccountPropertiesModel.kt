package com.abhijai.powerfulandroidapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhijai.powerfulandroidapp.AppConstants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = AppConstants.ACCOUNT_PROPERTIES_TABLE_NAME)
data class AccountPropertiesModel(
    // here autoGenerate is false for the PrimaryKey because we will be getting it from the server
    @SerializedName("pk")
    @Expose
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    var pk : Int,

    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email")
    var email : String,

    @SerializedName("username")
    @Expose
    @ColumnInfo(name = "username")
    var username : String

)