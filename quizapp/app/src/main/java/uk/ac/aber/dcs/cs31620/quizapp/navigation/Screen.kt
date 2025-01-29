package uk.ac.aber.dcs.cs31620.quizapp.navigation

sealed class Screen(
    val route: String
) {
    data object Home : Screen("home")
    data object Edit : Screen("edit")
}

val screens = listOf(
    Screen.Home,
    Screen.Edit
)