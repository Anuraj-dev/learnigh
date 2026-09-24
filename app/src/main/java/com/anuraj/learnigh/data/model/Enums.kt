package com.anuraj.learnigh.data.model

enum class SourceType(val label: String) {
    WEBSITE("Website"),
    APP_SUBSCRIPTION("App subscription"),
    YOUTUBE("YouTube"),
    OTHER_FREE("Other free"),
    PAID_COURSE("Paid course");

    companion object {
        fun fromName(name: String): SourceType =
            entries.find { it.name == name } ?: OTHER_FREE
    }
}

enum class CourseStatus(val label: String) {
    WISHLIST("Wishlist"),
    NOT_STARTED("Not started"),
    IN_PROGRESS("In progress"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
    EXPIRED("Expired");

    companion object {
        fun fromName(name: String): CourseStatus =
            entries.find { it.name == name } ?: NOT_STARTED
    }
}
