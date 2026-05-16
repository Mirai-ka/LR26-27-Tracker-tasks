package com.example.lr26_27_tracker_tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lr26_27_tracker_tasks.data.model.Task
import com.example.lr26_27_tracker_tasks.data.repository.TaskRepository
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskListState
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TaskRepository
    private lateinit var viewModel: TaskListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(TaskRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTasks should return success state when repository returns data`() = runTest {
        // Arrange
        val mockTasks = listOf(
            Task(id = "1", title = "Task 1", description = "Desc 1"),
            Task(id = "2", title = "Task 2", description = "Desc 2")
        )
        whenever(repository.getAllTasks()).thenReturn(Result.success(mockTasks))

        // Act
        viewModel = TaskListViewModel(repository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertTrue(state is TaskListState.Success)
        assertEquals(mockTasks.size, (state as TaskListState.Success).tasks.size)
    }

    @Test
    fun `loadTasks should return error state when repository fails`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        whenever(repository.getAllTasks()).thenReturn(Result.failure(Exception(errorMessage)))

        // Act
        viewModel = TaskListViewModel(repository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertTrue(state is TaskListState.Error)
        assertTrue((state as TaskListState.Error).message.contains(errorMessage))
    }

    @Test
    fun `deleteTask should call repository delete and reload tasks`() = runTest {
        // Arrange
        val mockTasks = listOf(Task(id = "1", title = "Task 1"))
        whenever(repository.getAllTasks()).thenReturn(Result.success(mockTasks))
        whenever(repository.deleteTask("1")).thenReturn(Result.success(Unit))

        viewModel = TaskListViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.deleteTask("1")
        advanceUntilIdle()

        // Assert
        verify(repository).deleteTask("1")
        verify(repository, org.mockito.Mockito.atLeast(2)).getAllTasks()
    }
}