package com.scavara.otium

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AmbientScreen() {
    // Placeholder data until we connect DynamoDB and S3
    // Note: You will need to drop two dummy images into your res/drawable folder
    // and name them placeholder_bg_1 and placeholder_bg_2, or change these IDs to an existing resource like R.drawable.ic_launcher_background
    val images = listOf(
        android.R.drawable.ic_menu_gallery, // Using default android icons temporarily so it compiles out of the box
        android.R.drawable.ic_menu_compass
    )
    val quotes = listOf(
        "Relax and let go.",
        "Breathe in the calm."
    )

    var currentIndex by remember { mutableIntStateOf(0) }

    // Timer: Cycles every 30 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L) 
            currentIndex = (currentIndex + 1) % images.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        
        // Pillar 1: Image Carousel
        Crossfade(
            targetState = images[currentIndex],
            animationSpec = tween(durationMillis = 2000), 
            label = "ImageCrossfade"
        ) { imageRes ->
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Ambient Background",
                contentScale = ContentScale.Crop, 
                modifier = Modifier.fillMaxSize()
            )
        }

        // TV Optimization: Gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 600f 
                    )
                )
        )

        // Pillar 2: Random Quote Overlay
        Crossfade(
            targetState = quotes[currentIndex],
            animationSpec = tween(durationMillis = 2000),
            label = "QuoteCrossfade",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 48.dp, end = 48.dp) 
        ) { quote ->
            Text(
                text = quote,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
