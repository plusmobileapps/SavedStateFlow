package com.plusmobileapps.savedstateflow

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plusmobileapps.savedstateflow.ui.theme.SavedStateFlowTheme
import kotlinx.coroutines.flow.StateFlow

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
                    ExampleContent(viewModel)
                }
            }
        }
    }
}

@Composable
fun ExampleContent(viewModel: MainViewModel) {
    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QueryTextField(stateFlow = viewModel.query, onValueChanged = viewModel::queryUpdated)
            LoadingUI(stateFlow = viewModel.isLoading)
            NewsArticlesUI(stateFlow = viewModel.newsArticles)
        }
    }
}

@Composable
fun QueryTextField(stateFlow: StateFlow<String>, onValueChanged: (String) -> Unit) {
    val state = stateFlow.collectAsState()
    OutlinedTextField(
        modifier = Modifier.padding(8.dp),
        value = state.value,
        label = { Text("Query") },
        onValueChange = onValueChanged
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingUI(stateFlow: StateFlow<Boolean>) {
    val state = stateFlow.collectAsState()
    AnimatedVisibility(modifier = Modifier.padding(8.dp), visible = state.value) {
        CircularProgressIndicator()
    }
}

@Composable
fun NewsArticlesUI(stateFlow: StateFlow<List<String>>) {
    val state = stateFlow.collectAsState()
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        items(state.value.size) { index ->
            val article = state.value[index]
            Column {
                Text(text = article)
                Divider()
            }
        }
    }
}