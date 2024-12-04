package com.an.jetpack_compose_playground.ui.component

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.util.lerp
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.absoluteValue

/**
 * A composable function that provides a reusable parallax-style pager.
 * Inspired by this article:
 * https://canopas.com/how-to-create-a-parallax-movie-pager-in-jetpack-compose-ab9c1e19d2cb
 *
 * @param items The list of items to display in the pager.
 * @param backgroundContent A composable that defines the content for the background layer of each page.
 * @param foregroundContent A composable that defines the content for the foreground (interactive) layer of each page.
 * @param modifier Modifier to be applied to the outer container of the pager.
 * @param pageCount The total number of pages in the pager. Defaults to the size of the items list.
 */
@Composable
fun <T> ParallaxPager(
    items: List<T>,
    imageLoader: (T) -> String,
    backgroundContent: @Composable (T, Float, Modifier) -> Unit,
    foregroundContent: @Composable BoxScope.(T, ImageBitmap?, Float) -> Unit,
    modifier: Modifier = Modifier,
    pageCount: Int = items.size
) {
    val backgroundPagerState = rememberPagerState(pageCount = { pageCount })
    val foregroundPagerState = rememberPagerState(pageCount = { pageCount })

    // Determine which pager is actively scrolling and pair it with the other pager for synchronized scrolling.
    // This is used to dynamically establish the "scrolling" and "following" pagers based on user interaction.
    val scrollingFollowingPair by remember {
        // Use derivedStateOf to ensure that recomposition only occurs when the scrolling state changes.
        derivedStateOf {
            when {
                // If the background pager is actively being scrolled, it is the "scrolling" pager,
                // and the foreground pager should follow it.
                backgroundPagerState.isScrollInProgress -> backgroundPagerState to foregroundPagerState

                // If the foreground pager is actively being scrolled, it is the "scrolling" pager,
                // and the background pager should follow it.
                foregroundPagerState.isScrollInProgress -> foregroundPagerState to backgroundPagerState

                // If neither pager is being scrolled, no pairing is required.
                else -> null
            }
        }
    }

    // Synchronize the scrolling of the background and foreground pagers based on user interaction.
    // The "scrolling" pager's state is observed and applied to the "second" pager to ensure a smooth sync.
    LaunchedEffect(scrollingFollowingPair) {
        scrollingFollowingPair?.let { (scrollingState, followingState) ->
            snapshotFlow { scrollingState.currentPage + scrollingState.currentPageOffsetFraction }
                .collect { pagePart ->
                    // Split the page position into an integer page index and a fractional offset.
                    val (page, offset) = BigDecimal.valueOf(pagePart.toDouble())
                        .divideAndRemainder(BigDecimal.ONE)  // Divide into whole and fractional parts.
                        .let { it[0].toInt() to it[1].toFloat() }

                    // Request the "second" pager to scroll to the same page and offset as the "scrolling" pager.
                    followingState.requestScrollToPage(page, offset)
                }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Background Pager
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = backgroundPagerState
        ) { currentPage ->
            // Calculate the offset of the current page relative to its default position.
            // This offset is used to create the parallax effect by modifying visual properties like translation.
            val currentPageOffset = calculatePageOffset(foregroundPagerState, currentPage)

            // Create a modifier to apply transformations based on the page's offset.
            // In this case, the page's horizontal position is adjusted to achieve a parallax effect.
            val modifier = Modifier.graphicsLayer(
                translationX = lerp(30f, 0f, 1f - currentPageOffset)
            )

            // Invoke the background content lambda to render the background for the current page.
            backgroundContent(items[currentPage], currentPageOffset, modifier)
        }

        GradientOverlay()

        // Foreground Pager
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = foregroundPagerState,
            verticalAlignment = Alignment.Bottom
        ) { currentPage ->
            // Calculate the offset of the current page relative to its default position.
            // This offset is used for visual transformations like scaling and translation.
            val currentPageOffset = calculatePageOffset(foregroundPagerState, currentPage)
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            // Mutable state to hold the loaded image bitmap.
            // This allows Compose to update the UI when the image is loaded.
            var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

            // Load the image for the current page asynchronously when the page changes.
            // Uses `LaunchedEffect` to ensure the loading logic is triggered only when the `currentPage` changes.
            LaunchedEffect(currentPage) {
                loadImageBitmap(context, coroutineScope, imageLoader(items[currentPage])) {
                    imageBitmap = it // Update the state with the loaded image.
                }
            }

            // Create a modifier for applying transformations to the foreground content.
            // The scale and translation values are adjusted based on the current page's offset
            // to create a zoom and parallax effect.
            val modifier = Modifier.graphicsLayer(
                scaleX = lerp(0.8f, 1f, 1f - currentPageOffset.absoluteValue.coerceIn(0f, 1f)),
                translationX = lerp(100f, 0f, 1f - currentPageOffset)
            )
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .background(Color.Black, shape = MaterialTheme.shapes.large)
            ) {
                foregroundContent(items[currentPage], imageBitmap, currentPageOffset)
            }
        }
    }
}

/**
 * A composable function to draw a gradient overlay on top of the background layer.
 *
 * This function is typically used to add a visual separation between a background
 * and other UI layers, ensuring that the foreground content remains visible
 * even if the background is visually busy or distracting.
 *
 * Uses a [Brush.verticalGradient] to create a gradient that transitions from a semi-transparent
 * black color to full transparency. The `startY` and `endY` values control the gradient's
 * vertical bounds, allowing customization of its reach on the screen.
 */
@Composable
private fun GradientOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                    startY = 0f,
                    endY = 500f
                )
            )
    )
}

/**
 * A composable function to create a card with parallax scrolling effects.
 *
 * @param item The data item associated with the card (generic type [T]).
 * @param imageBitmap The image to be displayed within the card, rendered with a parallax effect.
 * @param currentPageOffset The offset of the card relative to its default position, affecting
 * its visual transformations.
 * @param content A lambda to define the custom content of the card.
 * @param modifier A `Modifier` for additional customizations.
 *
 * - The card's `translationX` and `scaleX` are dynamically calculated based on the page offset,
 *   giving it a parallax-like effect as the user scrolls.
 * - A [Modifier.graphicsLayer] is used to apply these transformations efficiently.
 * - The [content] lambda ensures that different types of data can be easily displayed
 *   within the same layout.
 */
@Composable
fun <T> ParallaxCard(
    item: T,
    imageBitmap: ImageBitmap?,
    currentPageOffset: Float,
    content: @Composable BoxScope.(T, ImageBitmap?, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardTranslationX = lerp(100f, 0f, 1f - currentPageOffset)
    val cardScaleX = lerp(0.8f, 1f, 1f - currentPageOffset.absoluteValue.coerceIn(0f, 1f))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = cardScaleX
                translationX = cardTranslationX
            }
            .background(Color.Black, shape = MaterialTheme.shapes.large)
    ) {
        content(item, imageBitmap, currentPageOffset)
    }
}

/**
 * A composable function to render an image with a parallax effect.
 *
 * @param imageBitmap The image to be displayed.
 * @param currentPageOffset The offset of the page relative to its default position,
 * controlling the parallax effect.
 * @param modifier A [Modifier] for additional customizations.
 *
 * - The image is drawn onto a [Canvas], allowing precise control over its positioning.
 * - The `parallaxOffset` is calculated based on the current offset and screen width, ensuring
 *   a responsive and smooth parallax effect.
 * - The [Modifier.graphicsLayer] is used to further fine-tune the image's translation.
 */
@Composable
fun ParallaxImage(
    imageBitmap: ImageBitmap,
    currentPageOffset: Float,
    modifier: Modifier = Modifier
) {
    val drawSize = IntSize(imageBitmap.width, imageBitmap.height)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val parallaxOffset = currentPageOffset * screenWidth * 2f

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .border(2.dp, Color.White, MaterialTheme.shapes.large)
            .graphicsLayer { translationX = lerp(10f, 0f, 1f - currentPageOffset) }
    ) {
        translate(left = parallaxOffset) {
            drawImage(
                image = imageBitmap,
                srcSize = drawSize,
                dstSize = size.toIntSize(),
            )
        }
    }
}

/**
 * A composable function to render an overlay for a parallax card.
 *
 * @param title The title text to display on the overlay.
 * @param description The description text to display on the overlay.
 * @param currentPageOffset The offset of the page relative to its default position, controlling the overlay's animations.
 * @param modifier A `Modifier` for additional customizations.
 *
 * Implementation rationale:
 * - The [Box] creates a background gradient for the overlay, ensuring text readability.
 * - [Text] components are animated based on the [currentPageOffset], creating smooth transitions
 *   in their position and opacity.
 */
@Composable
fun ParallaxCardOverlay(
    title: String,
    description: String,
    currentPageOffset: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                    startY = 500f,
                    endY = 1000f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -(20).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val titleTranslationX = lerp(30f, 0f, 1f - currentPageOffset.absoluteValue.coerceIn(0f, 1f))
            val titleAlpha = lerp(0f, 1f, 1f - currentPageOffset.absoluteValue.coerceIn(0f, 1f))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .graphicsLayer {
                        translationX = titleTranslationX
                        alpha = titleAlpha
                    }
            )

            val descriptionTranslationX =
                lerp(150f, 0f, 1f - currentPageOffset.absoluteValue.coerceIn(0f, 1f))
            val descriptionAlpha = lerp(0f, 1f, 1f - currentPageOffset.absoluteValue.coerceIn(0f, 1f))

            Text(
                text = description,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .84f),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        translationX = descriptionTranslationX
                        alpha = descriptionAlpha
                    }
            )
        }
    }
}

private fun loadImageBitmap(
    context: Context,
    scope: CoroutineScope,
    imageUrl: String,
    onBitmapLoaded: (ImageBitmap?) -> Unit
) {
    scope.launch(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .build()

        when (val result = loader.execute(request)) {
            is SuccessResult -> {
                val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                onBitmapLoaded(bitmap?.asImageBitmap())
            }

            is ErrorResult -> {
                Log.e("ImageLoader", "Error loading image: ${result.throwable}")
                onBitmapLoaded(null)
            }
        }
    }
}


/**
 * Calculates the offset of the current page relative to its default position.
 *
 * @param state The state of the pager, used to retrieve the current page and its offset fraction.
 * @param currentPage The index of the page whose offset is being calculated.
 * @return A Float representing the page offset in the range [-1f, 1f].
 */
private fun calculatePageOffset(state: PagerState, currentPage: Int): Float {
    return (state.currentPage + state.currentPageOffsetFraction - currentPage).coerceIn(-1f, 1f)
}

