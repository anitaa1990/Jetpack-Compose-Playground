package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.an.jetpack_compose_playground.ui.component.ParallaxCard
import com.an.jetpack_compose_playground.ui.component.ParallaxCardOverlay
import com.an.jetpack_compose_playground.ui.component.ParallaxImage
import com.an.jetpack_compose_playground.ui.component.ParallaxPager

@Composable
fun ParallaxPagerScreen() {
    ParallaxPager(
        items = parallaxPagerList,
        imageLoader = { it.imageUrl }, // Specify how to extract the image URL
        backgroundContent = { movie, offset, modifier ->
            Image(
                painter = rememberAsyncImagePainter(movie.imageUrl),
                contentDescription = movie.title,
                contentScale = ContentScale.FillBounds,
                modifier = modifier.fillMaxSize()
            )
        },
        foregroundContent = { movie, imageBitmap, offset ->
            ParallaxCard(
                item = movie,
                imageBitmap = imageBitmap,
                currentPageOffset = offset,
                content = { movie, bitmap, offset ->
                    bitmap?.let {
                        ParallaxImage(it, offset)
                    }
                    ParallaxCardOverlay(movie.title, movie.description, offset)
                }
            )
        }
    )
}

private val parallaxPagerList = listOf<ParallaxPagerModel>(
    ParallaxPagerModel(
        title = "Moana 2",
        description = "After receiving an unexpected call from her wayfinding ancestors, Moana journeys alongside Maui and a new crew to the far seas of Oceania and into dangerous, long-lost waters for an adventure unlike anything she's ever faced.",
        imageUrl = "https://image.tmdb.org/t/p/w500/yh64qw9mgXBvlaWDi7Q9tpUBAvH.jpg"
    ),
    ParallaxPagerModel(
        title = "Gladiator II",
        description = "Years after witnessing the death of the revered hero Maximus at the hands of his uncle, Lucius is forced to enter the Colosseum after his home is conquered by the tyrannical Emperors who now lead Rome with an iron fist. With rage in his heart and the future of the Empire at stake, Lucius must look to his past to find strength and honor to return the glory of Rome to its people.",
        imageUrl = "https://image.tmdb.org/t/p/w500/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg"
    ),
    ParallaxPagerModel(
        title = "Wicked",
        description = "In the land of Oz, ostracized and misunderstood green-skinned Elphaba is forced to share a room with the popular aristocrat Glinda at Shiz University, and the two's unlikely friendship is tested as they begin to fulfill their respective destinies as Glinda the Good and the Wicked Witch of the West.",
        imageUrl = "https://image.tmdb.org/t/p/w500/xDGbZ0JJ3mYaGKy4Nzd9Kph6M9L.jpg"
    ),
    ParallaxPagerModel(
        title = "Red One",
        description = "After Santa Claus (codename: Red One) is kidnapped, the North Pole's Head of Security must team up with the world's most infamous bounty hunter in a globe-trotting, action-packed mission to save Christmas.",
        imageUrl = "https://image.tmdb.org/t/p/w500/cdqLnri3NEGcmfnqwk2TSIYtddg.jpg"
    ),
    ParallaxPagerModel(
        title = "Interstellar",
        description = "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
        imageUrl = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"
    )
)

data class ParallaxPagerModel(
    val title: String,
    val description: String,
    val imageUrl: String
)
