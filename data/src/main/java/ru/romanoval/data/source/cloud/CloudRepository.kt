package ru.romanoval.data.source.cloud

import retrofit2.Response
import ru.romanoval.data.model.ServerHabit
import ru.romanoval.data.restful.ApiService
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.Uid

class CloudRepository(
    private val api: ApiService
) : BaseCloudRepository {
    override suspend fun getAllHabits(): Response<List<ServerHabit>> =
        api.getHabits()

    override suspend fun deleteHabit(uid: Uid): Response<Unit> =
        api.deleteHabit(uid)

    override suspend fun postHabitDone(postDone: PostDone): Response<Unit> =
        api.postHabitDone(postDone)

    override suspend fun putHabit(habit: ServerHabit): Response<Uid> =
        api.putHabit(habit)
}