package uk.ac.aber.dcs.cs31620.quizapp.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.quizapp.navigation.NavRoute
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel
import uk.ac.aber.dcs.cs31620.quizapp.saving.Quiz

@Composable
fun QuizManagementScreen(
    navController: NavHostController,
    quizId: Int,
    quizViewModel: QuizViewModel
) {
    val quiz = quizViewModel.getQuizById(quizId)

    var title by remember { mutableStateOf(quiz?.title ?: "") }
    var subject by remember { mutableStateOf(quiz?.subject ?: "") }
    var description by remember { mutableStateOf(quiz?.description ?: "") }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Quiz") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Quiz Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subject Input
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Save Changes")
                }

                Button(
                    onClick = {
                        navController.navigate("${NavRoute.ManageQuestionsScreen.route}/$quizId")
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Manage Questions")
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (quiz != null) {
                                    quizViewModel.updateQuiz(
                                        quizIndex = quizId,
                                        title = title,
                                        subject = subject,
                                        description = description
                                    )
                                    navController.popBackStack()
                                }
                                showDialog = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Save Changes?") },
                    text = { Text("Are you sure you want to save the changes to this quiz?") }
                )
            }
        }
    }
}
