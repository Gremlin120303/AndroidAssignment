package uk.ac.aber.dcs.cs31620.quizapp.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.cs31620.quizapp.R
import uk.ac.aber.dcs.cs31620.quizapp.navigation.NavRoute
import uk.ac.aber.dcs.cs31620.quizapp.navigation.Screen
import uk.ac.aber.dcs.cs31620.quizapp.navigation.screens
import uk.ac.aber.dcs.cs31620.quizapp.theme.QuizappTheme

@Composable
fun MainPageNavigationBar(
    navController: NavHostController
) {
    val icons = mapOf(
        NavRoute.HomeScreen to IconGroup(
            filledIcon = Icons.Filled.Home,
            outlineIcon = Icons.Outlined.Home,
            label = stringResource(id = R.string.quiz)
        ),
        NavRoute.EditScreen to IconGroup(
            filledIcon = Icons.Filled.Edit,
            outlineIcon = Icons.Outlined.Edit,
            label = stringResource(id = R.string.edit)
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        listOf(NavRoute.HomeScreen, NavRoute.EditScreen).forEach { screen ->
            val iconGroup = icons[screen]
            val isSelected = currentDestination?.route == screen.route
            val labelText = iconGroup?.label ?: ""

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = (
                                if (isSelected)
                                    iconGroup?.filledIcon ?: Icons.Default.Home
                                else
                                    iconGroup?.outlineIcon ?: Icons.Default.Home),
                        contentDescription = labelText
                    )
                },
                label = { Text(labelText) },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun MainPageNavigationBarPreview() {
    val navController = rememberNavController()
    QuizappTheme(dynamicColor = false) {
        MainPageNavigationBar(navController)
    }
}