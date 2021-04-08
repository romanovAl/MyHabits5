package ru.romanoval.data.source.cloud

import retrofit2.Response
import ru.romanoval.data.model.ServerHabit
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.Uid

interface BaseCloudRepository {

    suspend fun getAllHabits(): Response<List<ServerHabit>>

    suspend fun deleteHabit(uid: Uid): Response<Unit>

    suspend fun postHabitDone(postDone: PostDone): Response<Unit>

    suspend fun putHabit(habit: ServerHabit): Response<Uid>

}