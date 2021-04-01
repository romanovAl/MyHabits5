package repository

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.ServerHabit
import ru.romanoval.domain.model.restful.Uid

interface AppRepository {
    //--------------------DATABASE-----------------------//

    suspend fun selectAllHabits() : List<Habit>
    suspend fun insertHabitIntoDB(habit: Habit)
    suspend fun updateHabitInDB(habit: Habit)
    suspend fun deleteHabitFromDB(habit : Habit)
    suspend fun deleteAllHabitsFromDB()

    //--------------------DATABASE-----------------------//

    //----------------------API--------------------------//

    suspend fun insertHabitIntoApi(habit : ServerHabit) : Response<Uid>
    suspend fun getHabitsFromApi() : Response<List<ServerHabit>>
    suspend fun postHabitDone(postDone : PostDone) : Response<Unit>
    suspend fun deleteHabitFromApi(uid : Uid) : Response<Unit>

    //----------------------API--------------------------//
}