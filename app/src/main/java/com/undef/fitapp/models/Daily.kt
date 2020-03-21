package com.undef.fitapp.models

import com.google.gson.annotations.SerializedName
import com.undef.fitapp.R

abstract class FoodNMet{
    abstract fun getTitle(): String
    abstract fun getKcals(): String
    abstract fun getIcon(): Int
}

data class Daily(
    @SerializedName("Burned")
    var burned: Double,
    @SerializedName("Consumed")
    var consumed: Double,
    @SerializedName("Food")
    var foods: List<Food>,
    @SerializedName("Met")
    var mets: List<Met>,
    @SerializedName("Remaining")
    var remaining: Double
)

data class Food(
    @SerializedName("Calories")
    var calories: Double,
    @SerializedName("Carbs")
    var carbs: Double,
    @SerializedName("Fat")
    var fat: Double,
    @SerializedName("Name")
    var name: String,
    @SerializedName("Protein")
    var protein: Double,
    @SerializedName("Quantity")
    var quantity: Double,
    @SerializedName("Unit1")
    var unit1: String,
    @SerializedName("Unit2")
    var unit2: String,
    @SerializedName("Unit3")
    var unit3: String



): FoodNMet() {
    override fun getTitle() = name

    override fun getKcals() = calories.toString()

    override fun getIcon() = R.drawable.ic_food
}

data class Met(
    @SerializedName("Detailed")
    var detailed: String,
    @SerializedName("MetNum")
    var metNum: Double
): FoodNMet() {
    override fun getTitle() = detailed

    override fun getKcals() = metNum.toString()

    override fun getIcon() = R.drawable.ic_exercise
}