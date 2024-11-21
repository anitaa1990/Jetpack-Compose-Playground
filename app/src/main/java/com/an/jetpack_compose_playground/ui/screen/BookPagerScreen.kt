package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.an.jetpack_compose_playground.ui.component.BookPager
import com.an.jetpack_compose_playground.ui.component.BookPagerOrientation

@Composable
fun BookPagerScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val pagerState = rememberPagerState { bookPagerList.size }
        BookPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            orientation = BookPagerOrientation.Vertical,
        ) { page ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp)),
            ) {
                BookPagerListItem(
                    modifier = Modifier.align(Alignment.Center),
                    model = bookPagerList[page]
                )
            }
        }
    }
}

@Composable
fun BookPagerListItem(
    modifier: Modifier = Modifier,
    model: BookPagerModel
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .widthIn(max = 480.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model.urlToImage)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = model.source.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .56f)
            )

            Text(
                text = model.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = model.description,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .84f)
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

private val bookPagerList = listOf<BookPagerModel>(
    BookPagerModel(
        source = "CNET",
        title = "Android 16 Will Launch Earlier Than Usual. Google's Android VP Explains Why",
        description = "Android 16 is likely to launch next year. However, the Android 15 QPR 1 beta release might have some hidden features in the pipeline for the next OS.",
        urlToImage = "https://cdn.mos.cms.futurecdn.net/jZKFCDntMzraETuRSpvG8E-1200-80.jpg"
    ),
    BookPagerModel(
        source = "The Verge",
        title = "Kids with Android phones will be able to use Google Wallet tap-to-pay soon",
        description = "Google will soon let children use Google Wallet and its tap-to-pay feature on Android phones, which debuted earlier this year with the Fitbit Ace LTE.",
        urlToImage = "https://cdn.vox-cdn.com/thumbor/a1UuqmTXeWu_sDyVAVipeGpIQ0s=/0x0:2040x1360/1200x628/filters:focal(1020x680:1021x681)/cdn.vox-cdn.com/uploads/chorus_asset/file/24016885/STK093_Google_04.jpg"
    ),
    BookPagerModel(
        source = "The Verge",
        title = "This pocket-friendly e-reader also wants to be your minimalist phone",
        description = "The Mudita Kompakt is a compact phone and e-reader with an E Ink screen and a custom version of Android designed to limit distractions.",
        urlToImage = "https://cdn.vox-cdn.com/thumbor/IdkLlBCS39mlECc0Mj1Ff9BY_6w=/0x0:2550x1700/1200x628/filters:focal(1275x850:1276x851)/cdn.vox-cdn.com/uploads/chorus_asset/file/25708430/kompakt1.jpg"
    ),
    BookPagerModel(
        source = "Gizmodo.com",
        title = "The Snapdragon 8 Elite Aims to Supercharge Android Performance",
        description = "The Snapdragon 8 Elite looks to beat the iPhone 16 and Qualcomm claims it’s still beating Intel’s Lunar Lake.",
        urlToImage = "https://gizmodo.com/app/uploads/2024/10/Snapdragon-8-Elite-Inside-View-QRD.jpg"
    ),
    BookPagerModel(
        source = "Android Central",
        title = "How to archive Android apps",
        description = "Manage your apps smartly by archiving apps that you don't use often, instead of uninstalling them and losing your data.",
        urlToImage = "https://cdn.mos.cms.futurecdn.net/BUkpFG877unnosZVLA49iD-1200-80.jpg"
    )
)

data class BookPagerModel(
    val source: String,
    val title: String,
    val description: String,
    val urlToImage: String
)
