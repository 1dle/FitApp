package com.undef.fitapp.models

abstract class Healthy(val id: Int, val title: String, val kcal: Double)

class Meal(id: Int, title: String, kcal: Double) : Healthy(id, title, kcal)

class Exercise(id: Int, title: String, kcal: Double) : Healthy(id, title, kcal)