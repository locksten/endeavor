import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CompleteDailyMutation
import com.example.endeavor.DailiesQuery
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.daily.CUpdateDailyModal
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun Daily(daily: DailiesQuery.Daily) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var isUpdateDialogOpen by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Theme.colors.graySurface.copy(
                    alpha = if (daily.isCompleted) {
                        0f
                    } else {
                        1f
                    }
                )
            )
            .combinedClickable(
                onClick = {
                    scope.launch {
                        completeDaily(gql, daily.id)
                    }
                },
                onLongClick = {
                    isUpdateDialogOpen = true
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        Arrangement.Start,
    ) {
        Text(
            text = daily.title,
            color = Theme.colors.onGraySurface,
            textDecoration = if (daily.isCompleted) {
                TextDecoration.LineThrough
            } else {
                null
            },
            fontSize = 20.sp,
            modifier = Modifier.alpha(
                if (daily.isCompleted) 0.5f else 1f,
            )
        )
        if (isUpdateDialogOpen) CUpdateDailyModal(daily) { isUpdateDialogOpen = false }
    }

}

suspend fun completeDaily(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            CompleteDailyMutation(id)
        ).await()
        gql.query(DailiesQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

/*
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun Daily(daily: DailiesQuery.Daily) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var isUpdateDialogOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Theme.colors.graySurface.copy(
                    alpha = if (daily.isCompleted) {
                        0f
                    } else {
                        1f
                    }
                )
            )
            .combinedClickable(
                onClick = {
                    scope.launch {
                        completeDaily(gql, daily.id)
                    }
                },
                onLongClick = {
                    isUpdateDialogOpen = true
                }
            )
            .padding(horizontal = 16.dp, vertical= 8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            Arrangement.Start,
        ) {
            Text(
                text = daily.title,
                color = Theme.colors.onGraySurface,
                textDecoration = if (daily.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                },
                fontSize = 20.sp,
                modifier = Modifier.alpha(
                    if (daily.isCompleted) 0.5f else 1f,
                )
            )
            if (isUpdateDialogOpen) CUpdateDailyModal(daily) { isUpdateDialogOpen = false }
        }
    }
}

 */