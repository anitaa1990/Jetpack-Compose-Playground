package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.an.jetpack_compose_playground.ui.component.CircleRevealPager

@Composable
fun CircleRevealPagerScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircleRevealPager(
            items = destinations,
            content = { destination, offset ->
                // Custom composable content for each page.
                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Add text content to the bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = destination.location,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                        )
                    )
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White)
                    )
                    Text(
                        modifier = Modifier
                            .padding(vertical = 10.dp),
                        text = destination.description,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                        )
                    )
                }
            }
        )
    }
}

private val destinations = listOf(
    CircleRevealModel("Paris", "City of Lights", "https://plus.unsplash.com/premium_photo-1661919210043-fd847a58522d?q=80&w=2971&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
    CircleRevealModel("New York", "The Big Apple", "https://images.unsplash.com/photo-1448317971280-6c74e016e49c?q=80&w=3032&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
    CircleRevealModel("Tokyo", "Land of the Rising Sun", "https://images.unsplash.com/photo-1554797589-7241bb691973?q=80&w=2836&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
    CircleRevealModel("India", "Land of Diversity", "https://images.unsplash.com/photo-1532664189809-02133fee698d?q=80&w=2952&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
    CircleRevealModel("South Africa", "Rainbow Nation", "https://images.unsplash.com/photo-1529528070131-eda9f3e90919?q=80&w=2835&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
)

data class CircleRevealModel(
    val location: String,
    val description: String,
    val imageUrl: String
)
