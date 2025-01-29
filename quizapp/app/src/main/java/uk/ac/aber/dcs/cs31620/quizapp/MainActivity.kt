package uk.ac.aber.dcs.cs31620.quizapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.cs31620.quizapp.home.HomeScreen
import uk.ac.aber.dcs.cs31620.quizapp.edit.EditScreen
import uk.ac.aber.dcs.cs31620.quizapp.navigation.NavGraph
import uk.ac.aber.dcs.cs31620.quizapp.navigation.Screen
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel
import uk.ac.aber.dcs.cs31620.quizapp.theme.QuizappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizappTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    BuildNavigationGraph()
                }
            }
        }
    }
}

@Composable
private fun BuildNavigationGraph() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application
    val viewModel = QuizViewModel(application)
    NavGraph(navController = navController, quizViewModel = viewModel)
}