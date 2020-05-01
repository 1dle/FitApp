package com.undef.fitapp.ui.mapexc

object ActivityCalculator {

    enum class ActivityType(val metNum: Double?){
        RUN(10.0),
        WALK(3.0),
        CYCLING(7.5),
        NONE(0.0)
    }
    //item.Duration * (item.MetNum * weight * 3.5) / 200.0
    fun burnedKcals(actType : ActivityType, minutes: Double, userWeight: Double) = minutes * (actType.metNum!! * userWeight * 3.5) / 200

}