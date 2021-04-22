package com.example.myhabits3.useCaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myhabits3.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import repository.AppRepository
import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.ResponseData
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.Uid
import useCases.api.DownloadHabitsFromApiUseCase

@RunWith(JUnit4::class)
class ApiUseCaseTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineScope =
        MainCoroutineScopeRule()

    @Mock
    lateinit var appRepository: AppRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        downloadHabitsFromApiUseCase = DownloadHabitsFromApiUseCase(appRepository)
    }


    @Mock
    private lateinit var habitFromApi: Habit
    private lateinit var habitsFromApi: MutableList<Habit>

    @Mock
    private lateinit var habitInDb: Habit

    private lateinit var habitsInDb: MutableList<Habit>

    @Mock
    private lateinit var downloadHabitsFromApiUseCase: DownloadHabitsFromApiUseCase


    @Test
    fun `DownloadHabitsFromApiUseCase test`() {

        habitsFromApi =
            mutableListOf(habitFromApi, habitFromApi, habitFromApi, habitFromApi, habitFromApi)//5
        habitsInDb =
            mutableListOf(
                habitInDb,
                habitInDb,
                habitInDb,
                habitInDb,
                habitInDb,
                habitInDb,
                habitInDb,
                habitInDb//8
            )

        runBlocking {
            `when`(appRepository.getHabitsFromApi())
                .thenReturn(
                    ResponseData.GetResponseData(habitsFromApi, null, true)
                )

            `when`(appRepository.deleteAllHabitsFromDB())
                .thenReturn(habitsInDb.clear())

            `when`(appRepository.insertHabitsIntoDB(habitsFromApi))
                .thenReturn(habitsFromApi.forEach {
                    habitsInDb.add(it)
                })

            downloadHabitsFromApiUseCase.execute().collect {

                assertEquals(true, it.isSuccessful)
                assertEquals(null, it.errorMessage)
                assertEquals(5, habitsInDb.size)

            }


            //---------------------get error----------------------
            habitsInDb =
                mutableListOf(
                    habitInDb,
                    habitInDb,
                    habitInDb,
                    habitInDb,
                    habitInDb,
                    habitInDb,
                    habitInDb,
                    habitInDb//8
                )

            `when`(appRepository.getHabitsFromApi())
                .thenReturn(
                    ResponseData.GetResponseData(null, "error", false)
                )

            downloadHabitsFromApiUseCase.execute().collect {

                assertEquals(false, it.isSuccessful)
                assertEquals("error", it.errorMessage)
                assertEquals(8, habitsInDb.size)

            }
        }
    }

    @Mock
    private lateinit var uidToDelete: Uid

    @Mock
    private lateinit var updatedHabit: Habit

    @Mock
    private lateinit var postDones: List<PostDone>

    @Test
    fun `LoadOnServerUseCase test`() {

        habitsFromApi =
            mutableListOf(habitFromApi, habitFromApi, habitFromApi, habitFromApi, habitFromApi)//5
        habitsInDb = mutableListOf(
            habitInDb, habitInDb, habitInDb, habitInDb
        )

        runBlocking {
            `when`(appRepository.getHabitsFromApi())
                .thenReturn(
                    ResponseData.GetResponseData(habitsFromApi, null, true)
                )

            `when`(appRepository.deleteHabitFromApi(uidToDelete)).thenReturn(
                ResponseData(isSuccessful = true, errorMessage = null)
            )

            `when`(appRepository.selectAllHabitsFromDB()).thenReturn(
                habitsInDb
            )

            `when`(appRepository.putHabitIntoApi(habitInDb))
                .thenReturn(ResponseData.PutResponseData(uidToDelete, null, true))

            `when`(appRepository.updateHabitInDB(habitInDb))
                .then {
                    for (i in 0..habitsInDb.size) {
                        if (habitsInDb[i].bdId == updatedHabit.bdId) {
                            habitsInDb.removeAt(i)
                            habitsInDb.add(i, updatedHabit)
                        }
                    }

                }

            `when`(appRepository.postHabitsDone(postDones))
                .thenReturn(
                    ResponseData(null, true)
                )

            downloadHabitsFromApiUseCase.execute().collect {

                assertEquals(true, it.isSuccessful)
                assertEquals(null, it.errorMessage)

            }

            //----------------select error------------------

            `when`(appRepository.getHabitsFromApi())
                .thenReturn(
                    ResponseData.GetResponseData(null, "error", false)
                )

            downloadHabitsFromApiUseCase.execute().collect {

                assertEquals(false, it.isSuccessful)
                assertEquals("error", it.errorMessage)

            }

            //----------------select error------------------

            //---------------- error------------------

            `when`(appRepository.getHabitsFromApi())
                .thenReturn(
                    ResponseData.GetResponseData(habitsFromApi, null, true)
                )

            `when`(appRepository.deleteHabitFromApi(uidToDelete)).thenReturn(
                ResponseData(isSuccessful = true, errorMessage = null)
            )

        }

    }

}