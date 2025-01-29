package uk.ac.aber.dcs.cs31620.quizapp.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel
import uk.ac.aber.dcs.cs31620.quizapp.components.TopLevelScaffold
import uk.ac.aber.dcs.cs31620.quizapp.navigation.NavRoute
import uk.ac.aber.dcs.cs31620.quizapp.theme.QuizappTheme


// In HomeScreen.kt
@Composable
fun HomeScreen(navController: NavHostController, quizViewModel: QuizViewModel) {
    val quizzes by quizViewModel.quizzes.observeAsState(emptyList())

    TopLevelScaffold(navController = navController) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                quizzes.filter { it.title.isNotEmpty() && it.subject.isNotEmpty() && it.description.isNotEmpty() }.forEachIndexed { index, quiz ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Title: ${quiz.title}", style = MaterialTheme.typography.titleLarge)
                            Text("Subject: ${quiz.subject}", style = MaterialTheme.typography.bodyMedium)
                            Text("Description: ${quiz.description}", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { navController.navigate(NavRoute.QuizScreen.route.replace("{quizIndex}", quiz.id.toString())) },
                                modifier = Modifier.align(Alignment.Start)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start Quiz")
                                Text("Start Quiz", modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

