package com.undef.fitapp.api.model
import com.google.gson.annotations.SerializedName


data class UserData(
    @SerializedName("ID")
    var id: Int,
    @SerializedName("BirthDate")
    var birthDate: String,
    @SerializedName("Email")
    var email: String,
    @SerializedName("Height")
    var height: Double,
    @SerializedName("Male")
    var male: String,
    @SerializedName("Name")
    var name: String,
    @SerializedName("Password")
    var password: String,
    @SerializedName("RegisterDate")
    var registerDate: String,
    @SerializedName("Weight")
    var weight: Double,
    @SerializedName("Goal")
    var goal: Int
)