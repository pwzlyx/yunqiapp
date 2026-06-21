package com.yunqi.app.domain.reminder

enum class DailyReminderType(val defaultTime: String) {
    Weight("09:00"),
    FetalMovement("20:00"),
    Vitamin("09:00"),
    Water("13:00"),
    Exercise("17:00"),
    Custom("20:00"),
}
