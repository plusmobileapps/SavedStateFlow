package com.plusmobileapps.savedstateflowhilt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plusmobileapps.savedstateflowhilt.ui.theme.SavedStateFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavedStateFlowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state = viewModel.state.collectAsState()
                    ExampleContent(state.value, viewModel::updateQuery)
                }
            }
        }
    }
}

@Composable
fun ExampleContent(state: MainViewModel.State, onQueryChanged: (String) -> Unit) {
    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QueryTextField(query = state.query, onValueChanged = onQueryChanged)
            LoadingUI(state.isLoading)
            NewsArticlesUI(state.results)
        }
    }
}

@Composable
fun QueryTextField(query: String, onValueChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.padding(8.dp),
        value = query,
        label = { Text("Query") },
        onValueChange = onValueChanged
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingUI(isLoading: Boolean) {
    AnimatedVisibility(modifier = Modifier.padding(8.dp), visible = isLoading) {
        CircularProgressIndicator()
    }
}

@Composable
fun NewsArticlesUI(state: List<String>) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        items(state.size) { index ->
            val article = state[index]
            Column {
                Text(text = article)
                Divider()
            }
        }
    }
}