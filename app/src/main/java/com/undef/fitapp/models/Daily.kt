package com.undef.fitapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.undef.fitapp.R
import com.undef.fitapp.ui.custom.ItemType
import kotlinx.android.parcel.Parcelize

abstract class FoodNMet{
    abstract fun getTitle(): String
    abstract fun getKcals(): String
    abstract fun getIcon(): Int
    abstract fun getDateOfAdd(): String?
    abstract val type: ItemType
}

data class Daily(
    @SerializedName("Burned")
    var burned: Double,
    @SerializedName("Comsumed")
    var consumed: Double,
    @SerializedName("Food")
    var foods: List<Food>,
    @SerializedName("Met")
    var mets: List<Met>,
    @SerializedName("Remaining")
    var remaining: Double
)
@Parcelize
data class Food(
    @SerializedName("ID")
    var id: Int,
    @SerializedName("Calories")
    var calories: Double,
    @SerializedName("Carbs")
    var carbs: Double,
    @SerializedName("Date")
    var date: String?,
    @SerializedName("Fat")
    var fat: Double,
    @SerializedName("Name")
    var name: String,
    @SerializedName("Protein")
    var protein: Double,
    @SerializedName("Quantity")
    var quantity: Double
): FoodNMet(), Parcelable{
    override fun getTitle() = name

    override fun getKcals() = calories.toString()

    override fun getIcon() = R.drawable.ic_food

    override fun getDateOfAdd() = date

    override val type: ItemType
        get() = ItemType.FOOD
}

data class Met(
    @SerializedName("ID")
    var id: Int,
    @SerializedName("Date")
    var date: String?,
    @SerializedName("Detailed")
    var detailed: String,
    @SerializedName("Duration")
    var duration: Double,
    @SerializedName("MetNum", alternate = arrayOf<String>("MET"))
    var metNum: Double
): FoodNMet() {
    override fun getTitle() = detailed

    override fun getKcals() = metNum.toString()

    override fun getIcon() = R.drawable.ic_exercise

    override fun getDateOfAdd() = date

    override val type: ItemType
        get() = ItemType.EXERCISE
}