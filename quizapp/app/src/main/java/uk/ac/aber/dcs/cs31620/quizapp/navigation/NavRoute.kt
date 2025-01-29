package uk.ac.aber.dcs.cs31620.quizapp.navigation

enum class NavRoute(val route: String) {
    HomeScreen("home"),
    EditScreen("edit"),
    QuizManagementScreen("quiz_management"),
    ManageQuestionsScreen("manage_questions"),
    QuizScreen("quiz/{quizIndex}"),
    FinalScreen("results");

    companion object {
        fun fromRoute(route: String): NavRoute =
            entries.firstOrNull { it.route == route } ?: HomeScreen
    }
}