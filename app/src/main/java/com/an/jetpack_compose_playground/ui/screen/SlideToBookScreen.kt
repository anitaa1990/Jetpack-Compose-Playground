package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.an.jetpack_compose_playground.R
import com.an.jetpack_compose_playground.ui.common.MainScaffold
import com.an.jetpack_compose_playground.ui.component.SlideToBookButton

@Composable
fun SlideToBookScreen() {
    MainScaffold(R.string.btn_txt_slide_to_book) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            SlideToBookButton(
                btnText = stringResource(R.string.btn_slide_to_book),
                btnTextStyle = TextStyle(
                    fontFamily = FontFamily.Default,
                    lineHeight = 20.sp,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFFFAC901),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                ),
                outerBtnBackgroundColor = Color.Black,
                sliderBtnBackgroundColor = Color(0xFFF5B63B),
                sliderBtnIcon = R.drawable.ic_thumb_car,
                onBtnSwipe = {  }
            )
        }
    }
}