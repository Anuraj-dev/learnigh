package com.anuraj.learnigh.ui.navigation

object Routes {
    const val HOME = "home"
    const val COURSES = "courses"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{courseId}"
    const val EDIT = "edit?courseId={courseId}"
    const val ADD = "edit"

    fun detail(id: Long) = "detail/$id"
    fun edit(id: Long?) = if (id == null) "edit" else "edit?courseId=$id"
}
