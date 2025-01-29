package uk.ac.aber.dcs.cs31620.quizapp.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.ac.aber.dcs.cs31620.quizapp.R
import uk.ac.aber.dcs.cs31620.quizapp.theme.QuizappTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageTopAppBar(
    onclick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name))
        },

    )
}

@Preview
@Composable
private fun MainPageTopAppBarPreview() {
    QuizappTheme(dynamicColor = false) {
        MainPageTopAppBar()
    }
}