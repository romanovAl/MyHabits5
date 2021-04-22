package com.example.myhabits3.useCaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myhabits3.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import repository.AppRepository
import retrofit2.http.DELETE
import ru.romanoval.domain.model.Habit
import useCases.database.*

@RunWith(JUnit4::class)
class DBUseCaseTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineScope =
        MainCoroutineScopeRule()

    @Mock
    lateinit var appRepository: AppRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        getHabitsDBUseCase = GetHabitsFromDBUseCase(appRepository)
        deleteHabitsDBUseCase = DeleteHabitFromDBUseCase(appRepository)
        insertHabitDBUseCase = InsertHabitIntoDBUseCase(appRepository)
        insertHabitsDBUseCase = InsertHabitsIntoDBUseCase(appRepository)
        updateHabitDBUseCase = UpdateHabitInDBUseCase(appRepository)

    }

    @Mock
    lateinit var mockedAllHabits: List<Habit>
    private lateinit var getHabitsDBUseCase: GetHabitsFromDBUseCase

    @Test
    fun `GetHabitsDbUseCase test`() {

        runBlocking {
            Mockito.`when`(appRepository.selectAllHabitsFromDB()).thenReturn(mockedAllHabits)

            delay(1000)

            Assert.assertEquals(mockedAllHabits, getHabitsDBUseCase.execute())
        }

    }

    @Mock
    lateinit var habitToDelete: Habit

    @Mock
    lateinit var mockedHabit: Habit

    @Mock
    lateinit var deleteHabitsDBUseCase: DeleteHabitFromDBUseCase

    @Test
    fun `DeleteHabitUseCase test`() {

        val mockedHabitsInDb: MutableList<Habit> =
            mutableListOf(habitToDelete, mockedHabit, mockedHabit)


        runBlocking {
            Mockito.`when`(appRepository.deleteHabitFromDB(habitToDelete)).thenAnswer {
                mockedHabitsInDb.remove(
                    habitToDelete
                )
            }
            deleteHabitsDBUseCase.execute(habitToDelete)

            delay(1000)

            Assert.assertEquals(2, mockedHabitsInDb.size)
        }
    }

    private lateinit var insertHabitDBUseCase: InsertHabitIntoDBUseCase

    @Test
    fun `InsertHabitDBUseCase test`() {

        val mockedHabitsInDb: MutableList<Habit> = mutableListOf()

        runBlocking {
            Mockito.`when`(appRepository.insertHabitIntoDB(mockedHabit))
                .thenAnswer { mockedHabitsInDb.add(mockedHabit) }

            insertHabitDBUseCase.execute(mockedHabit)
            delay(1000)
            insertHabitDBUseCase.execute(mockedHabit)
            delay(1000)
            Assert.assertEquals(2, mockedHabitsInDb.size)
        }

    }

    private lateinit var insertHabitsDBUseCase: InsertHabitsIntoDBUseCase

    @Test
    fun `InsertHabitsDBUseCase test`() {

        val mockedHabitsInDb: MutableList<Habit> = mutableListOf()

        val mockedHabitsToInsert =
            listOf(mockedHabit, mockedHabit, mockedHabit, mockedHabit, mockedHabit) //5

        runBlocking {
            Mockito.`when`(appRepository.insertHabitIntoDB(mockedHabit))
                .thenAnswer { mockedHabitsInDb.add(mockedHabit) }

            insertHabitsDBUseCase.execute(mockedHabitsToInsert)
            delay(2000)
            Assert.assertEquals(5, mockedHabitsInDb.size)
        }

    }

    private lateinit var updateHabitDBUseCase: UpdateHabitInDBUseCase

    @Mock
    lateinit var mockedUpdatedHabit: Habit

    @Test
    fun `updateHabitUseCase test`() {
        val mockedHabitsInDb: MutableList<Habit> = mutableListOf(mockedHabit)


        runBlocking {
            Mockito.`when`(appRepository.updateHabitInDB(mockedHabit)).thenAnswer {
                mockedHabitsInDb.add(mockedUpdatedHabit)
                mockedHabitsInDb.remove(mockedHabit)
            }

            updateHabitDBUseCase.execute(mockedHabit)
            delay(1000)

        }
        Assert.assertEquals(mockedUpdatedHabit, mockedHabitsInDb[0])
    }


}