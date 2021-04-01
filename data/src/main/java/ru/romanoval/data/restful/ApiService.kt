package ru.romanoval.data.restful

import retrofit2.Response
import retrofit2.http.*
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.ServerHabit
import ru.romanoval.domain.model.restful.Uid

interface ApiService {

    @GET("api/habit")
    suspend fun getHabits(
    ): Response<List<ServerHabit>>

    @HTTP(method = "DELETE", path = "api/habit", hasBody = true)
    suspend fun deleteHabit(
        @Body uid: Uid
    ): Response<Unit>

    @POST("api/habit_done")
    suspend fun postHabitDone(
        @Body postDone: PostDone
    ): Response<Unit>

    @PUT("api/habit")
    suspend fun putHabit(
        @Body habit: ServerHabit
    ): Response<Uid>

}