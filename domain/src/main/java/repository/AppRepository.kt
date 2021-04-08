package repository

import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.ResponseData
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.Uid

interface AppRepository {
    //--------------------DATABASE-----------------------//

    suspend fun selectAllHabitsFromDB() : List<Habit>
    suspend fun insertHabitIntoDB(habit: Habit)
    suspend fun insertHabitsIntoDB(habits : List<Habit>)
    suspend fun updateHabitInDB(habit: Habit)
    suspend fun deleteHabitFromDB(habit : Habit)
    suspend fun deleteAllHabitsFromDB()

    //--------------------DATABASE-----------------------//

    //----------------------API--------------------------//

    suspend fun putHabitIntoApi(habit : Habit) : ResponseData.PutResponseData
    suspend fun getHabitsFromApi() : ResponseData.GetResponseData
    suspend fun postHabitDone(postDone : PostDone) : ResponseData
    suspend fun deleteHabitFromApi(uid : Uid) : ResponseData
    suspend fun postHabitsDone(postDones : List<PostDone>) : ResponseData

    //----------------------API--------------------------//
}