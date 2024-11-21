package com.an.jetpack_compose_playground.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * BookPager is a composable function that creates a flip-style pager component with horizontal
 * or vertical orientations. It simulates the effect of flipping through pages in a book, with
 * additional features like overscroll effects.
 *
 * @param state Manages the current page state and scrolling behavior.
 * @param modifier A modifier to customize the pager's layout and visuals.
 * @param orientation Determines the scrolling direction (horizontal or vertical).
 * @param pageContent Lambda defining the content of each page.
 */
@Composable
fun BookPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    orientation: BookPagerOrientation,
    pageContent: @Composable (Int) -> Unit
) {
    // Tracks the overscroll value for when the user scrolls beyond the limits of the pages.
    // Using `remember` ensures this value persists during recompositions.
    val overscrollAmount = remember { mutableFloatStateOf(0f) }

    // Reset the overscroll amount to zero when scrolling stops.
    // `snapshotFlow` listens to `isScrollInProgress` changes from the PagerState.
    LaunchedEffect(Unit) {
        snapshotFlow { state.isScrollInProgress }.collect {
            if (!it) overscrollAmount.floatValue = 0f
        }
    }

    // Smoothly animates the overscroll effect using spring animation.
    val animatedOverscrollAmount by animateFloatAsState(
        targetValue = overscrollAmount.floatValue / 500,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = ""
    )

    // Creates a NestedScrollConnection to handle overscroll logic specific to the orientation.
    val nestedScrollConnection = rememberBookPagerOverscroll(
        orientation = orientation,
        overscrollAmount = overscrollAmount
    )

    // Select the pager implementation (VerticalPager or HorizontalPager) based on the orientation.
    when (orientation) {
        BookPagerOrientation.Vertical -> {
            VerticalPager(
                state = state,
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection),
                pageContent = {
                    PagerContent(
                        it,
                        state,
                        orientation,
                        pageContent,
                        animatedOverscrollAmount
                    )
                }
            )
        }

        BookPagerOrientation.Horizontal -> {
            HorizontalPager(
                state = state,
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection),
                pageContent = {
                    PagerContent(
                        it,
                        state,
                        orientation,
                        pageContent,
                        animatedOverscrollAmount
                    )
                }
            )
        }
    }
}

/**
 * PagerContent handles the content and animations for each individual page.
 * It ensures that pages are layered properly and handles the zIndex and transformation.
 *
 * @param page The index of the page.
 * @param state The PagerState to determine page offsets and scrolling behavior.
 * @param orientation The orientation (vertical or horizontal) of the pager.
 * @param pageContent The content composable for each page.
 * @param animatedOverscrollAmount The animated value for overscroll effects.
 */
@Composable
private fun PagerContent(
    page: Int,
    state: PagerState,
    orientation: BookPagerOrientation,
    pageContent: @Composable (Int) -> Unit,
    animatedOverscrollAmount: Float
) {
    // A state to manage the zIndex of the current page.
    // Higher zIndex brings the page to the top.
    var zIndex by remember { mutableFloatStateOf(0f) }

    // Dynamically calculate and update zIndex based on the page's offset.
    LaunchedEffect(Unit) {
        snapshotFlow { state.offsetForPage(page) }.collect {
            zIndex = when (state.offsetForPage(page)) {
                in -.5f..(.5f) -> 3f    // Topmost layer for the active page
                in -1f..1f -> 2f        // Middle layer for adjacent pages
                else -> 1f                  // Background layer for distant pages
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .graphicsLayer {
                val pageOffset = state.offsetForPage(page)
                when (orientation) {
                    BookPagerOrientation.Vertical -> translationY = size.height * pageOffset
                    BookPagerOrientation.Horizontal -> translationX = size.width * pageOffset
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // remember the state for caching the bitmap of the page for rendering effects
        var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }
        val graphicsLayer = rememberGraphicsLayer()
        val isImageBitmapNull by remember {
            derivedStateOf {
                imageBitmap == null
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                // Hide content during scroll if the bitmap is available
                .alpha(if (state.isScrollInProgress && !isImageBitmapNull) 0f else 1f)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                },
            contentAlignment = Alignment.Center
        ) {
            pageContent(page)
        }

        LaunchedEffect(state.isScrollInProgress) {
            while (true) {
                if (graphicsLayer.size.width != 0)
                    imageBitmap = graphicsLayer.toImageBitmap()
                delay(if (state.isScrollInProgress) 16 else 300)
            }
        }

        LaunchedEffect(MaterialTheme.colorScheme.surface) {
            if (graphicsLayer.size.width != 0)
                imageBitmap = graphicsLayer.toImageBitmap()
        }

        // Adding page-flap effects for page transitions, creating a visual book-like experience.
        PageFlap(
            modifier = Modifier.fillMaxSize(),
            pageFlap = when (orientation) {
                BookPagerOrientation.Vertical -> PageFlapType.Top
                BookPagerOrientation.Horizontal -> PageFlapType.Left
            },
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverscrollAmount = { animatedOverscrollAmount }
        )

        PageFlap(
            modifier = Modifier.fillMaxSize(),
            pageFlap = when (orientation) {
                BookPagerOrientation.Vertical -> PageFlapType.Bottom
                BookPagerOrientation.Horizontal -> PageFlapType.Right
            },
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverscrollAmount = { animatedOverscrollAmount }
        )
    }
}

/**
 * Creates a NestedScrollConnection to handle overscroll effects during paging.
 * NestedScrollConnection allows for low-level control over scroll events, making it possible
 * to handle overscroll effects dynamically.
 *
 * This function allows the pager to handle "overscroll" interactions when a user scrolls past
 * the first or last page. It adjusts the overscroll amount dynamically based on the user’s
 * scroll behavior, providing a fluid visual effect.
 *
 * @param orientation Determines the direction of scrolling (vertical or horizontal).
 * @param overscrollAmount A mutable state that tracks the overscroll offset value.
 * @return A NestedScrollConnection that reacts to scroll events.
 */
@Composable
private fun rememberBookPagerOverscroll(
    orientation: BookPagerOrientation,
    overscrollAmount: MutableFloatState
): NestedScrollConnection {
    // remember ensures that the NestedScrollConnection is recomposed
    // only when the orientation changes.
    val nestedScrollConnection = remember(orientation) {
        object : NestedScrollConnection {

            /**
             * Adjusts the overscroll value based on the scroll distance (`available`).
             * A damping factor (0.3f) is applied to make the overscroll effect smoother.
             * This reduces the sensitivity of the overscroll effect, ensuring it feels smooth
             * and intuitive.
             */
            private fun calculateOverscroll(available: Float) {
                val previous = overscrollAmount.floatValue
                overscrollAmount.floatValue += available * (.3f)

                // Constrain the overscroll value to ensure a natural feel.
                // The coerceAtLeast and coerceAtMost methods prevent the overscroll from
                // flipping directions abruptly, maintaining a natural user experience.
                overscrollAmount.floatValue = when {
                    previous > 0 -> overscrollAmount.floatValue.coerceAtLeast(0f)
                    previous < 0 -> overscrollAmount.floatValue.coerceAtMost(0f)
                    else -> overscrollAmount.floatValue
                }
            }

            /**
             * Called before the scroll event is processed.
             * Handles overscroll adjustments when the user scrolls beyond the limits.
             */
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (overscrollAmount.floatValue != 0f) {
                    // Adjust overscroll based on the scrolling orientation.
                    when (orientation) {
                        BookPagerOrientation.Vertical -> calculateOverscroll(available.y)
                        BookPagerOrientation.Horizontal -> calculateOverscroll(available.x)
                    }
                    return available
                }

                return super.onPreScroll(available, source)
            }

            /**
             * Called after the scroll event is processed.
             * Reacts to the remaining scroll distance (`available`) and updates the overscroll value.
             */
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Update the overscroll amount based on the scroll orientation.
                when (orientation) {
                    BookPagerOrientation.Vertical -> calculateOverscroll(available.y)
                    BookPagerOrientation.Horizontal -> calculateOverscroll(available.x)
                }
                return available
            }
        }
    }
    return nestedScrollConnection
}

sealed class BookPagerOrientation {
    data object Vertical : BookPagerOrientation()
    data object Horizontal : BookPagerOrientation()
}

/**
 * Calculates the offset for a specific page based on its position relative to the current page.
 *
 * This function determines how far a given page is from the currently active page,
 * expressed as a fractional offset. It is used for animations like transitions and layering.
 *
 * @param page The index of the page for which to calculate the offset.
 * @return The fractional offset of the page relative to the current page.
 */
private fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

/**
 * Computes the starting offset for a page, ensuring it does not go below 0.
 *
 * This function is useful for animations that depend on the initial transition state of a page.
 */
private fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

/**
 * Computes the ending offset for a page, ensuring it does not exceed 0.
 *
 * This function is used to handle animations as the page transitions out of view.
 */
private fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}

/**
 * Renders the "flap" effect for pages during transitions, simulating the turning of a physical book
 *
 * The flap is rendered using a combination of rotation and clipping, with the direction of the flap
 * (top, bottom, left, or right) determined by the page orientation.
 *
 * @param pageFlap The side of the page to animate (e.g., top or left).
 * @param imageBitmap The cached bitmap of the page for rendering.
 * @param state The current PagerState.
 * @param page The index of the page.
 * @param animatedOverscrollAmount The animated overscroll value.
 */
@Composable
private fun BoxScope.PageFlap(
    modifier: Modifier = Modifier,
    pageFlap: PageFlapType,
    imageBitmap: () -> ImageBitmap?,
    state: PagerState,
    page: Int,
    animatedOverscrollAmount: () -> Float = { 0f },
) {
    // Provides access to screen density for size calculations
    val density = LocalDensity.current

    // Dynamically calculates the size of the flap based on the image bitmap dimensions.
    val size by remember {
        derivedStateOf {
            imageBitmap()?.let {
                with(density) {
                    DpSize(it.width.toDp(), it.height.toDp())
                }
            } ?: DpSize.Zero
        }
    }
    Canvas(
        modifier
            .size(size)
            .align(Alignment.TopStart)
            .graphicsLayer {
                shape = pageFlap.shape // Applies a shape to clip the flap
                clip = true // Ensures the flap is clipped to the shape bounds
                cameraDistance = 65f // Adds depth to the rotation effect

                // Rotate the flap based on the orientation and offset.
                when (pageFlap) {
                    is PageFlapType.Top -> {
                        rotationX = min(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverscrollAmount().coerceAtLeast(0f) * -20f
                        )
                    }

                    is PageFlapType.Bottom -> {
                        rotationX = max(
                            (state.startOffsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverscrollAmount().coerceAtMost(0f) * -20f
                        )
                    }

                    is PageFlapType.Left -> {
                        rotationY = -min(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverscrollAmount().coerceAtLeast(0f) * -20f
                        )
                    }

                    is PageFlapType.Right -> {
                        rotationY = -max(
                            (state.startOffsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverscrollAmount().coerceAtMost(0f) * -20f
                        )
                    }
                }
            }
    ) {
        // Draw the cached bitmap for the flap.
        imageBitmap()?.let { imageBitmap ->
            drawImage(imageBitmap)
            drawImage(
                imageBitmap,
                colorFilter = ColorFilter.tint(
                    Color.Black.copy(
                        alpha = when (pageFlap) {
                            PageFlapType.Top, PageFlapType.Left -> max(
                                (state.endOffsetForPage(page).absoluteValue * .9f).coerceIn(
                                    0f..1f
                                ), animatedOverscrollAmount() * .3f
                            )

                            PageFlapType.Bottom, PageFlapType.Right -> max(
                                (state.startOffsetForPage(page) * .9f).coerceIn(
                                    0f..1f
                                ), (animatedOverscrollAmount() * -1) * .3f
                            )
                        },
                    )
                )
            )
        }
    }
}

/**
 * Represents the side of the page to which the flap effect is applied.
 *
 * Each type corresponds to a specific part of the page (e.g., Top, Bottom, Left, Right) that is
 * animated during a transition. This allows for flexible handling of both vertical and horizontal
 * page orientations.
 *
 * @property shape The shape that defines the clipping region for the flap.
 */
internal sealed class PageFlapType(val shape: Shape) {
    // Represents the top half of the page (used for vertical orientation).
    data object Top : PageFlapType(TopShape)

    // Represents the bottom half of the page (used for vertical orientation).
    data object Bottom : PageFlapType(BottomShape)

    // Represents the left half of the page (used for horizontal orientation).
    data object Left : PageFlapType(LeftShape)

    // Represents the right half of the page (used for horizontal orientation).
    data object Right : PageFlapType(RightShape)
}

/**
 * Defines the shape for the top half of a page.
 *
 * This shape is used to clip the upper region of the page during transitions
 * in vertical orientation. It effectively splits the page in half and applies animations
 * like rotation or scaling.
 */
private val TopShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, 0f, size.width, size.height / 2))
}

private val BottomShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, size.height / 2, size.width, size.height))
}

private val LeftShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
}

private val RightShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(size.width / 2, 0f, size.width, size.height))
}
