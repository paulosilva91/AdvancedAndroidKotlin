package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DefaultITasksRepositoryTest{
    private val task1 = Task("Title1", "Description 1")
    private val task2 = Task("Title2", "Description 2")
    private val task3 = Task("Title3", "Description 3")

    private val remoteTasks = listOf(task1,task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var  taskRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    private lateinit var tasksRepository: DefaultITasksRepository

    @Before
    fun createRepository(){
        taskRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())

        tasksRepository = DefaultITasksRepository(taskRemoteDataSource,tasksLocalDataSource, Dispatchers.Unconfined)
    }
    @ExperimentalCoroutinesApi
    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() = runBlockingTest {
        val tasks = tasksRepository.getTasks(true) as Result.Success
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
}