package com.an.jetpack_compose_playground.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * SlideToBookButton
 *
 * This composable creates a custom, swipeable button that resembles a "slide to book" interaction.
 * It is composed of two main parts:
 *  - The outer track (the full-width button background) which displays the label.
 *  - The inner slider thumb, which can be dragged from left to right.
 *
 *
 * @param btnText Text to display on the outer button track (e.g., "Book Ride ₹199")
 * @param btnTextStyle Text style for the button label (e.g., font weight, color)
 * @param outerBtnBackgroundColor Background color for the full-width outer button
 * @param sliderBtnBackgroundColor Background color for the draggable thumb button
 * @param sliderBtnIcon Icon shown inside the slider thumb (e.g., car or arrow icon)
 * @param onBtnSwipe Callback triggered once the user slides to complete the booking
 */
@Composable
fun SlideToBookButton(
    btnText: String,
    btnTextStyle: TextStyle,
    outerBtnBackgroundColor: Color,
    sliderBtnBackgroundColor: Color,
    @DrawableRes sliderBtnIcon: Int,
    onBtnSwipe: () -> Unit
) {
    val density = LocalDensity.current

    // Slider button width
    val sliderButtonWidthDp = 70.dp

    // Convert slider button width into pixels so we can use it in math
    val sliderButtonWidthPx = with(density) { sliderButtonWidthDp.toPx() }

    // Current horizontal position of the slider button (in pixels)
    var sliderPositionPx by remember { mutableFloatStateOf(0f) }

    // Total width of the outer button in pixels (captured during layout)
    var boxWidthPx by remember { mutableIntStateOf(0) }

    //  Flag to show the loading indicator
    var showLoading by remember { mutableStateOf(false) }

    // Calculate drag progress percentage (0f to 1f)
    val dragProgress = remember(sliderPositionPx, boxWidthPx) {
        if (boxWidthPx > 0) {
            (sliderPositionPx / (boxWidthPx - sliderButtonWidthPx)).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    // Alpha value for the button label — 1 when untouched, fades to 0 as drag progresses
    val textAlpha = 1f - dragProgress

    // Flag to indicate the slide is complete
    var sliderComplete by remember { mutableStateOf(false) }

    // Animate the shrinking of the outer track
    val trackScale by animateFloatAsState(
        targetValue = if (sliderComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 300), label = "trackScale"
    )

    // Animate the fading of the slider button
    val sliderAlpha by animateFloatAsState(
        targetValue = if (sliderComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 300), label = "sliderAlpha"
    )

    // Mark slide complete once drag passes 80%
    LaunchedEffect(dragProgress) {
        if (dragProgress >= 0.8f && !sliderComplete) {
            sliderComplete = true
            showLoading = true
            onBtnSwipe()
        }
    }

    // The root layout for the button — stretches full width and has fixed height
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .onSizeChanged { size ->
                // Capture the full width of the button once it's laid out
                boxWidthPx = size.width
            }
    ) {
        // Outer track — acts as the base of the button
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(scaleX = trackScale, scaleY = 1f) // 👈 Animate shrink
                .background(
                    color = outerBtnBackgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // The center-aligned button label
            Text(
                text = btnText,
                style = btnTextStyle,
                modifier = Modifier.align(Alignment.Center)
                    .alpha(textAlpha) // Applies the dynamic transparency to the label

            )
        }

        // Slider thumb container, positioned at the left edge of the button
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(1.dp)
                // Shift the slider button based on drag position (px to dp conversion)
                .offset(x = with(density) { sliderPositionPx.toDp() })
                // Handle horizontal drag gestures
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // Calculate new potential position
                        val newPosition = sliderPositionPx + delta
                        // Clamp it within 0 to (totalWidth - slider button width)
                        val maxPosition = boxWidthPx - sliderButtonWidthPx
                        sliderPositionPx = newPosition.coerceIn(0f, maxPosition)
                    },
                    onDragStarted = { /* Optional: add feedback or animation here */ },
                    onDragStopped = { }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .alpha(sliderAlpha) // 👈 Fade out as slide completes
                    .graphicsLayer { alpha = sliderAlpha }
            ) {
                // The draggable thumb itself
                SliderButton(
                    sliderBtnWidth = sliderButtonWidthDp,
                    sliderBtnBackgroundColor = sliderBtnBackgroundColor,
                    sliderBtnIcon = sliderBtnIcon
                )
            }
        }

        // Show the loading indicator after the slider reaches the end and the animation completes
        if (showLoading && trackScale.toInt() == 0) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        }
    }
}

/**
 * SliderButton
 *
 * This composable defines the visual appearance of the slider thumb — a small rounded box
 * that contains an icon (usually a car or arrow). It is positioned inside the larger
 * SlideToBookButton and will later be made draggable.
 *
 * @param sliderBtnBackgroundColor Background color for the thumb (distinct from the track)
 * @param sliderBtnIcon Icon displayed at the center of the thumb button
 */
@Composable
private fun SliderButton(
    sliderBtnWidth: Dp, // Width of the button
    sliderBtnBackgroundColor: Color, // Background color for the button
    @DrawableRes sliderBtnIcon: Int  // Icon shown inside the button
) {
    // Root Box for the slider button
    Box(
        modifier = Modifier
            .wrapContentSize()
            .width(sliderBtnWidth)
            .height(54.dp)
            .background(
                color = sliderBtnBackgroundColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(sliderBtnIcon),
                contentDescription = "Car Icon",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
