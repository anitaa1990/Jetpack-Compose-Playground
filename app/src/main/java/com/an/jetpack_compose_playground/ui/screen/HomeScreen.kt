package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.an.jetpack_compose_playground.AppConstants.ROUTE_BOOK_PAGER
import com.an.jetpack_compose_playground.AppConstants.ROUTE_CIRCLE_REVEAL_PAGER
import com.an.jetpack_compose_playground.AppConstants.ROUTE_COMPOSE_TEXT_EDITOR
import com.an.jetpack_compose_playground.AppConstants.ROUTE_PARALLAX_PAGER
import com.an.jetpack_compose_playground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                },
                modifier = Modifier,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.text_home_screen),
                style = MaterialTheme.typography.bodyLarge
            )

            DemoButtonWithText(
                buttonTextRes = R.string.btn_txt_compose_text_editor,
                infoTextRes = R.string.info_txt_compose_text_editor,
                onClick = { navController.navigate(ROUTE_COMPOSE_TEXT_EDITOR) }
            )

            DemoButtonWithText(
                buttonTextRes = R.string.btn_txt_book_pager,
                infoTextRes = R.string.info_txt_book_pager,
                onClick = { navController.navigate(ROUTE_BOOK_PAGER) }
            )

            DemoButtonWithText(
                buttonTextRes = R.string.btn_txt_parallax_pager,
                infoTextRes = R.string.info_txt_parallax_pager,
                onClick = { navController.navigate(ROUTE_PARALLAX_PAGER) }
            )

            DemoButtonWithText(
                buttonTextRes = R.string.btn_txt_circle_reveal_pager,
                infoTextRes = R.string.info_txt_circle_reveal_pager,
                onClick = { navController.navigate(ROUTE_CIRCLE_REVEAL_PAGER) }
            )
        }
    }
}

@Composable
private fun DemoButtonWithText(
    buttonTextRes: Int,
    infoTextRes: Int,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .wrapContentWidth()
                .padding(end = 10.dp),
            onClick = { onClick() },
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(id = buttonTextRes))
        }
        Text(
            text = stringResource(infoTextRes),
            fontSize = 14.sp,
            modifier = Modifier.padding(6.dp),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.outline
        )
    }
}