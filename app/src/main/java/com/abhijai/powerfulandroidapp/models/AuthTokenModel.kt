package com.abhijai.powerfulandroidapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.abhijai.powerfulandroidapp.AppConstants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A Foreign Key is a column or a combination of columns whose values match a Primary Key in a different table.
 *
 * The relationship between 2 tables matches the Primary Key in one of the tables with a Foreign Key in the
 * second table.
 */
// AuthTokenModel has foreign key relationship with AccountPropertiesModel

@Entity(
    tableName = AppConstants.AUTH_TOKEN_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = AccountPropertiesModel::class,
            parentColumns = ["pk"],
            childColumns = ["account_pk"],
            onDelete = CASCADE  //referentialIntegrity -> if AccountPropertiesTable get deleted than AuthTokenTable will also be get deleted
        )
    ]
)
data class AuthTokenModel(
    @PrimaryKey
    @ColumnInfo(name = "account_pk")
    var account_pk:Int ? = -1,

    @SerializedName("token")
    @Expose
    @ColumnInfo(name = "token")
    var token : String ? = null

)