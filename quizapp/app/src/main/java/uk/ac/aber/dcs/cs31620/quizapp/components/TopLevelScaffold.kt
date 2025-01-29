package uk.ac.aber.dcs.cs31620.quizapp.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController


@Composable
fun TopLevelScaffold(
    navController: NavHostController,
    content: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    Scaffold(
        topBar = {
            MainPageTopAppBar()
        },
        bottomBar = {
            MainPageNavigationBar(navController)
        },
        content = {innerPadding -> content(innerPadding) }
    )
}