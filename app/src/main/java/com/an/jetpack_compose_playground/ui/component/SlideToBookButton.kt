package com.an.jetpack_compose_playground.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun SlideToBookButton(
    btnText: String,
    btnTextStyle: TextStyle,
    outerBtnBackgroundColor: Color,
    sliderBtnBackgroundColor: Color,
    @DrawableRes sliderBtnIcon: Int,
    onBtnSwipe: () -> Unit
) {

}