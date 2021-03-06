package ru.hse.miem.miemapp.data.api

data class AuthRequest(
    val authCode: String
)

data class VacancyApplyRequest(
    val about_me: String,
    val vacancy_id: Long
)

data class ApplicationConfirmRequest(
    val application_id: Long,
    val student_confirm: Int
)