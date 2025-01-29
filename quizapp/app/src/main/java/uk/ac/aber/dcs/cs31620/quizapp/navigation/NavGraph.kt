package uk.ac.aber.dcs.cs31620.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uk.ac.aber.dcs.cs31620.quizapp.edit.EditScreen
import uk.ac.aber.dcs.cs31620.quizapp.edit.ManageQuestionsScreen
import uk.ac.aber.dcs.cs31620.quizapp.home.HomeScreen
import uk.ac.aber.dcs.cs31620.quizapp.edit.QuizManagementScreen
import uk.ac.aber.dcs.cs31620.quizapp.quiz.FinalScreen
import uk.ac.aber.dcs.cs31620.quizapp.quiz.QuizScreen
import uk.ac.aber.dcs.cs31620.quizapp.saving.QuizViewModel



@Composable
fun NavGraph(navController: NavHostController, quizViewModel: QuizViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.HomeScreen.route
    ) {
        addHomeScreen(navController, quizViewModel, this)

        addEditScreen(navController, quizViewModel,this)

        addQuizManagementScreen(navController, quizViewModel,this)

        addManageQuestionsScreen(navController, quizViewModel, this)

        addQuizScreen(navController, quizViewModel, this)

        addFinalScreen(navController, this, quizViewModel)
    }
}

private fun addHomeScreen(
    navController: NavHostController,
    quizViewModel: QuizViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = NavRoute.HomeScreen.route) {
        HomeScreen(navController = navController, quizViewModel = quizViewModel)
    }
}

private fun addEditScreen(
    navController: NavHostController,
    quizViewModel: QuizViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.EditScreen.route,
        arguments = listOf(
            navArgument("NewQuiz") {
                type = NavType.StringType
                nullable = true
            }
        )
    ) { backStackEntry ->
        val newQuiz = backStackEntry.arguments?.getString("NewQuiz")
        EditScreen(navController = navController, quizViewModel = quizViewModel)
    }
}

private fun addQuizManagementScreen(
    navController: NavHostController,
    quizViewModel: QuizViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.QuizManagementScreen.route + "/{quizId}",
        arguments = listOf(navArgument("quizId") {
            type = NavType.IntType
        })
    ) { backStackEntry ->
        val quizId = backStackEntry.arguments?.getInt("quizId") ?: 0
        QuizManagementScreen(navController = navController, quizId = quizId, quizViewModel = quizViewModel)
    }
}

private fun addManageQuestionsScreen(
    navController: NavHostController,
    quizViewModel: QuizViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.ManageQuestionsScreen.route + "/{quizId}",
        arguments = listOf(navArgument("quizId") {
            type = NavType.IntType
        })
    ) { backStackEntry ->
        val quizId = backStackEntry.arguments?.getInt("quizId") ?: 0
        ManageQuestionsScreen(navController = navController, quizId = quizId, quizViewModel = quizViewModel)
    }
}

private fun addQuizScreen(
    navController: NavHostController,
    quizViewModel: QuizViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.QuizScreen.route,
        arguments = listOf(navArgument("quizIndex") {
            type = NavType.IntType
        })
    ) { backStackEntry ->
        val quizId = backStackEntry.arguments?.getInt("quizIndex") ?: 0
        QuizScreen(navController = navController, quizViewModel = quizViewModel, quizId = quizId)
    }
}

private fun addFinalScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    quizViewModel: QuizViewModel
) {
    navGraphBuilder.composable(route = NavRoute.FinalScreen.route) {
        FinalScreen(navController = navController, quizViewModel = quizViewModel)
    }
}


