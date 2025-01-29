package uk.ac.aber.dcs.cs31620.quizapp.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.quizapp.components.TopLevelScaffold
import uk.ac.aber.dcs.cs31620.quizapp.navigation.NavRoute
import uk.ac.aber.dcs.cs31620.quizapp.saving.Quiz
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel

// In EditScreen.kt
@Composable
fun EditScreen(navController: NavHostController, quizViewModel: QuizViewModel) {
    TopLevelScaffold(navController = navController) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val quizzes = quizViewModel.quizzes.value.orEmpty()
                    items(quizzes) { quiz ->
                        QuizItem(quiz = quiz, onEditClick = {
                            navController.navigate("${NavRoute.QuizManagementScreen.route}/${quiz.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun QuizItem(quiz: Quiz, onEditClick: () -> Unit) {
    // Layout for displaying quiz title, subject, and description with an Edit button
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Display Quiz Title, Subject, and Description
            Text(text = "Title: ${quiz.title}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Subject: ${quiz.subject}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Description: ${quiz.description}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            // Edit Button next to each quiz
            Button(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Quiz",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(text = "Edit")
            }
        }
    }
}
