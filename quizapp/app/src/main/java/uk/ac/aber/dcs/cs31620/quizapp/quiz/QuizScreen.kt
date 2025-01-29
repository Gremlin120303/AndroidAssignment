package uk.ac.aber.dcs.cs31620.quizapp.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.quizapp.navigation.NavRoute
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizDatabase

@Composable
fun QuizScreen(navController: NavController, quizViewModel: QuizViewModel, quizId: Int) {
    val currentQuiz = quizViewModel.getQuizById(quizId)
    val currentQuestion = currentQuiz?.questions?.getOrNull(quizViewModel.currentQuestionIndex)

    var selectedOption by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(currentQuiz?.title ?: "Take Quiz")  // Show quiz title or fallback text
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            if (currentQuestion != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = currentQuestion.question, fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
                    currentQuestion.answers.forEach { answer ->
                        Row(
                            modifier = Modifier.fillMaxWidth().selectable(
                                selected = (answer == selectedOption),
                                onClick = { selectedOption = answer }
                            ).padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (answer == selectedOption), onClick = { selectedOption = answer })
                            Text(text = answer, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (selectedOption.isNotEmpty() &&
                                selectedOption == currentQuiz?.questions?.getOrNull(quizViewModel.currentQuestionIndex)?.correctAnswer) {
                                quizViewModel.score++
                            }

                            if (quizViewModel.currentQuestionIndex < currentQuiz?.questions?.size?.minus(1) ?: 0) {
                                quizViewModel.nextQuestion()
                            } else {
                                quizViewModel.finishQuiz()
                                navController.navigate(NavRoute.FinalScreen.route)
                            }
                        }
                    ) {
                        Text(
                            if (quizViewModel.currentQuestionIndex < currentQuiz.questions.size - 1)
                                "Save and Next"
                            else
                                "Submit and Finish Quiz"
                        )
                    }
                }
            } else {
                Text(
                    text = "Screen not found",
                    modifier = Modifier.fillMaxSize(),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}