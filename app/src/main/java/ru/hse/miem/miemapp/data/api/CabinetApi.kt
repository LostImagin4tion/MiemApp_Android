package ru.hse.miem.miemapp.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CabinetApi {

    companion object {
        const val CABINET_BASE_URL = "https://cabinet.miem.hse.ru/"
        const val DEFAULT_CHAT_LINK = "https://chat.miem.hse.ru/"
        const val SUCCESS_CODE_PREFIX = 20
        private const val api = "api"
        private const val publicApi = "public-api"
        private const val baseURL = "devcabinet.miem.vmnet.top"

        fun getAvatarUrl(userId: Long) = "$CABINET_BASE_URL$publicApi/user/$userId/avatar"
        fun getProjectUrl(projectId: Long) = "$CABINET_BASE_URL#/project/$projectId"
    }

    @POST("vue/google")
    suspend fun auth(@Body authRequest: AuthRequest): AuthResponse

    /**
     * Profile related endpoints
     */
    @GET("$api/student_profile")
    suspend fun myStudentProfile(): StudentProfileResponse

    @GET("$api/teacher_profile")
    suspend fun myTeacherProfile(): TeacherProfileResponse

    @GET("$publicApi/student_profile/{id}")
    suspend fun studentProfile(@Path("id") id: Long): StudentProfileResponse

    @GET("$publicApi/teacher_profile/{id}")
    suspend fun teacherProfile(@Path("id") id: Long): TeacherProfileResponse

    @GET("$publicApi/student_statistics/{id}")
    suspend fun userStatistic(@Path("id") id: Long): StatisticResponse

    @GET("$api/student/projects/and/applications/my")
    suspend fun myUserStatistic(): MyStatisticResponse

    @POST("$api/student/application/confirm")
    suspend fun applicationConfirm(@Body request: ApplicationConfirmRequest)

    @GET("$publicApi/badge/user/{userId}/progress")
    suspend fun achievementsWithProgress(@Path("userId") id: Long): AchievementsResponse

    @GET("$publicApi/git_statistics/student/{userId}")
    suspend fun studentGitStatistics(@Path("userId") id: Long): UserGitStatisticsResponse

    @GET("$publicApi/git_statistics/teacher/{userId}")
    suspend fun teacherGitStatistics(@Path("userId") id: Long): UserGitStatisticsResponse

    @GET("$api/student_statistics/git/my")
    suspend fun myUserGitStatistics(): UserGitStatisticsResponse

    /**
     * Project related endpoints
     */
    @GET("$publicApi/project/header/{id}")
    suspend fun projectHeader(@Path("id") id: Long): ProjectHeaderResponse

    @GET("$publicApi/project/body/{id}")
    suspend fun projectBody(@Path("id") id: Long): ProjectBodyResponse

    @GET("$publicApi/project/students/{id}")
    suspend fun projectMembers(@Path("id") id: Long): ProjectMembersResponse

    @GET("$api/project/vacancies/{id}")
    suspend fun projectVacancies(@Path("id") id: Long): ProjectVacanciesResponse

    @GET("$publicApi/project/vacancies/{id}")
    suspend fun projectVacanciesPublic(@Path("id") id: Long): ProjectVacanciesResponse

    @GET("$publicApi/git_statistics/project/{id}")
    suspend fun gitStatistics(@Path("id") id: Long): GitStatisticsResponse

    @POST("$api/student/application/add")
    suspend fun applyForVacancy(@Body request: VacancyApplyRequest)

    /**
     * Search related endpoints
     */
    @GET("$publicApi/projects")
    suspend fun allProjects(): ProjectsAllResponse

    @GET("$publicApi/vacancy/list")
    suspend fun allVacancies(): VacanciesAllResponse

    /**
     * Schedule related endpoints
     */
    @GET("$publicApi/user/email/{email}")
    suspend fun userInfoByEmail(@Path("email") email: String): UserInfoResponse

    /**
     * Sandbox related endpoints
     */

    @GET("$publicApi/sandbox")
    suspend fun projectSandbox(): SandboxResponse
}