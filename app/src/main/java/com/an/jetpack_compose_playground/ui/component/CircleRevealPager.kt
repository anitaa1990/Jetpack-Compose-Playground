package com.an.jetpack_compose_playground.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.sqrt

/**
 * A composable function that creates a horizontally scrollable pager with a circular reveal animation.
 *
 * @param items The list of items to display in the pager.
 * @param content A lambda function to define the composable content for each page.
 * @param modifier A [Modifier] to customize the appearance and layout of the pager.
 *
 * This function handles animations, transformations, and touch interactions, leaving the content
 * definition flexible so that we can customise it.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> CircleRevealPager(
    items: List<T>,
    content: @Composable BoxScope.(T, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Tracks the state of the pager, such as the current page and scrolling offsets.
    val state = rememberPagerState(pageCount = { items.size })

    // Tracks the vertical touch offset (Y-axis) for defining the origin of the circular reveal animation.
    var offsetY by remember { mutableFloatStateOf(0f) }

    HorizontalPager(
        state = state,
        modifier = modifier
            .pointerInteropFilter { // Intercepts touch events to track the Y-offset for the circular reveal effect.
                offsetY = it.y
                false // Passes the event to the pager.
            }
            .clip(RoundedCornerShape(25.dp)) // Clips the pager content with rounded corners.
            .background(Color.Black), // Sets a background color for the pager.
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { // Applies animations and transformations.
                    val pageOffset = state.offsetForPage(page) // Calculates the horizontal offset for the current page.

                    // Translate the page horizontally based on its scroll offset.
                    translationX = size.width * pageOffset

                    // Define the circular shape for the reveal effect.
                    val endOffset = state.endOffsetForPage(page)
                    shadowElevation = 20f // Adds a shadow to give a depth effect.
                    shape = CirclePath( // Uses the custom CirclePath shape for the circular reveal.
                        progress = 1f - endOffset.absoluteValue, // Progress of the animation.
                        origin = Offset(size.width, offsetY) // Origin of the circular reveal (touch position).
                    )
                    clip = true // Ensures that content outside the circular shape is clipped.

                    // Apply scaling based on the offset to create a zoom effect.
                    val absoluteOffset = pageOffset.absoluteValue
                    val scale = 1f + (absoluteOffset * 0.4f)
                    scaleX = scale
                    scaleY = scale

                    // Adjust the transparency of pages as they move out of focus.
                    val startOffset = state.startOffsetForPage(page)
                    alpha = (2f - startOffset) / 2f
                },
            contentAlignment = Alignment.Center
        ) {
            // Render custom content for each page, defined by the caller.
            content(items[page], state.offsetForPage(page))
        }
    }
}

/**
 * A custom shape class that creates a circular reveal effect.
 *
 * @param progress The progress of the animation (0.0 to 1.0), where 0 is fully hidden and 1 is fully revealed.
 * @param origin The origin of the circular reveal effect, specified as an [Offset] on the screen.
 *
 * This shape dynamically calculates the center and radius of a circle based on the progress and
 * creates an outline using a circular path. It's useful for creating animated reveals.
 */
private class CirclePath(
    private val progress: Float,
    private val origin: Offset = Offset(0f, 0f)
) : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        // Calculate the center of the circle based on the animation progress and origin.
        val center = Offset(
            x = size.center.x - ((size.center.x - origin.x) * (1f - progress)),
            y = size.center.y - ((size.center.y - origin.y) * (1f - progress)),
        )

        // Calculate the radius of the circle based on the size and progress.
        val radius = (sqrt(size.height * size.height + size.width * size.width) * 0.5f) * progress

        // Create an outline using the calculated circular path.
        return Outline.Generic(Path().apply {
            addOval(Rect(center = center, radius = radius))
        })
    }
}

/**
 * Calculates the offset for a given page relative to the current page.
 *
 * @param page The index of the page to calculate the offset for.
 * @return A [Float] representing the offset, where negative values mean the page is before the current page,
 *         and positive values mean the page is after the current page.
 */
private fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

/**
 * Calculates the start offset for a given page.
 *
 * @param page The index of the page to calculate the start offset for.
 * @return A [Float] representing the offset, coerced to a minimum of 0.
 */
private fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

/**
 * Calculates the end offset for a given page.
 *
 * @param page The index of the page to calculate the end offset for.
 * @return A [Float] representing the offset, coerced to a maximum of 0.
 */
private fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}
