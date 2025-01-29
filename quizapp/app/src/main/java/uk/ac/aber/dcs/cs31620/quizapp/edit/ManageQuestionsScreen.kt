package uk.ac.aber.dcs.cs31620.quizapp.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel
import uk.ac.aber.dcs.cs31620.quizapp.saving.Question

@Composable
fun ManageQuestionsScreen(
    navController: NavHostController,
    quizId: Int,
    quizViewModel: QuizViewModel
) {
    val quiz = quizViewModel.getQuizById(quizId)

    if (quiz == null) {
        Text("Quiz not found")
        return
    }

    val questions = quiz.questions
    var currentIndex by remember { mutableStateOf(0) }

    var currentQuestion by remember {
        mutableStateOf(questions.getOrNull(currentIndex) ?: Question("", listOf(""), ""))
    }

    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun updateQuestion(updated: Question) {
        if (updated.answers.size < 2) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("A question must have at least 2 answers")
            }
            return
        }
        val updatedList = questions.toMutableList()
        if (currentIndex in updatedList.indices) {
            updatedList[currentIndex] = updated
            quizViewModel.updateQuestionsForQuiz(quizId, updatedList)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Manage Questions", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentQuestion.question,
                onValueChange = { currentQuestion = currentQuestion.copy(question = it) },
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = currentQuestion.answers.joinToString(","),
                onValueChange = {
                    currentQuestion = currentQuestion.copy(answers = it.split(","))
                },
                label = { Text("Answers (comma-separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = currentQuestion.correctAnswer,
                onValueChange = { currentQuestion = currentQuestion.copy(correctAnswer = it) },
                label = { Text("Correct Answer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { updateQuestion(currentQuestion) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Save Question")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    if (currentIndex > 0) {
                        currentIndex--
                        currentQuestion = questions.getOrNull(currentIndex) ?: currentQuestion
                    }
                }, enabled = currentIndex > 0) {
                    Text("Previous")
                }

                Button(onClick = {
                    if (currentIndex < questions.size - 1) {
                        currentIndex++
                        currentQuestion = questions.getOrNull(currentIndex) ?: currentQuestion
                    }
                }, enabled = currentIndex < questions.size - 1) {
                    Text("Next")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    if (questions.size < 10) {
                        quizViewModel.addQuestionToQuiz(quizId, Question("", listOf(""), ""))
                        currentIndex = questions.size
                        currentQuestion = Question("", listOf(""), "")
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Cannot add more than 10 questions")
                        }
                    }
                }) {
                    Text("Add Question")
                }

                Button(onClick = {
                    if (questions.isNotEmpty()) {
                        val updatedList = questions.toMutableList()
                        if (currentIndex in updatedList.indices) {
                            updatedList.removeAt(currentIndex)
                            quizViewModel.updateQuestionsForQuiz(quizId, updatedList)
                            currentIndex = (currentIndex - 1).coerceAtLeast(0)
                            currentQuestion = updatedList.getOrNull(currentIndex) ?: Question("", listOf(""), "")
                        }
                    }
                }) {
                    Text("Delete Question")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
        Box(modifier = Modifier.padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter) {
            Button(
                onClick = { showDialog = true },
            ) {
                Text("Save and Exit")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Save") },
                text = { Text("Are you sure you want to save and exit?") },
                confirmButton = {
                    TextButton(onClick = {
                        quizViewModel.updateQuestionsForQuiz(quizId, questions)
                        showDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
