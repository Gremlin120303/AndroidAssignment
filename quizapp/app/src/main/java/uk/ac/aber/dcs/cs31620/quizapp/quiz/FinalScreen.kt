package uk.ac.aber.dcs.cs31620.quizapp.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.quizapp.navigation.NavRoute
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel

@Composable
fun FinalScreen(navController: NavHostController, quizViewModel: QuizViewModel) {
    val score = quizViewModel.score
    val pastScores = quizViewModel.pastScores
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Final Score: ${if (pastScores.isNotEmpty()) pastScores.first() else 0}", fontSize = 32.sp, modifier = Modifier.padding(bottom = 16.dp))
            Text(text = "Past Scores", fontSize = 24.sp, modifier = Modifier.padding(bottom = 4.dp))

            pastScores.forEachIndexed { index, pastScore ->
                Text(
                    text = "Attempt ${index + 1}: $pastScore",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                quizViewModel.resetQuiz()
                navController.navigate(NavRoute.HomeScreen.route) { popUpTo(NavRoute.FinalScreen.route) { inclusive = true } }
            }) {
                Text("Back to Home")
            }
        }
    }
}
