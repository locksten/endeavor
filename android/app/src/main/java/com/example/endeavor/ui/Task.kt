package com.example.endeavor.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.NormalizedCache
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateTaskMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.TasksQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/*
@Preview(
    name = "Light Mode",
    device = Devices.PIXEL_2
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun UsersPreview() {
    EndeavorTheme {
        UserList(testUsers)
    }
}

val testUsers = listOf(
    UserSearchQuery.UserSearch(id = "1", username = "username", createdAt = "date"),
    UserSearchQuery.UserSearch(id = "2", username = "username2", createdAt = "date")
)
*/

@Composable
fun Task(task: TasksQuery.Task) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        Arrangement.SpaceBetween,
    ) {
        Text(
            text = task.title,
            color = Theme.colors.text,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Text(
            text = "${task.isCompleted}",
            color = Theme.colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Text(
            text = "id: ${task.id}",
            color = Theme.colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
    }
}

@Composable
fun TaskList(tasks: List<TasksQuery.Task>) {
    LazyColumn(
        Modifier.fillMaxSize()
    ) {
        items(tasks) {
            Task(it)
        }
    }
}

@Composable
fun CTaskList() {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current

    val tasks = gqlWatchQuery(TasksQuery())?.me?.tasks

    Column(verticalArrangement = Arrangement.Top) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            tasks?.let { TaskList(it) }
        }
        Button(
            onClick = {
                scope.launch {
                    try {
                        gql.mutate(
                            CreateTaskMutation(
                                createTaskTitle = "new Title",
                                createTaskDifficulty = 9
                            )
                        ).await().data?.createTask
                        gql.query(TasksQuery()).await()
                    } catch (e: ApolloNetworkException) {
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Create")
        }
    }
}