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
    // Phase 1 Placeholder Images
    val images = listOf(
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_compass
    )

    var currentIndex by remember { mutableIntStateOf(0) }

    // State holding the live quote, initialized with a fallback
    var currentQuote by remember {
        mutableStateOf(QuoteResponse("Breathing in, I calm body and mind.", "Thich Nhat Hanh", ""))
    }

    // Master Timer: Fetches a new quote and cycles the image every 30 seconds
    LaunchedEffect(Unit) {
        // Initial fetch so we don't wait 30 seconds for the first live quote
        try {
            currentQuote = QuoteApi.service.getRandomQuote()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        while (true) {
            delay(30_000L)

            // Cycle the background image
            currentIndex = (currentIndex + 1) % images.size

            // Fetch the next live quote from Heroku
            try {
                currentQuote = QuoteApi.service.getRandomQuote()
            } catch (e: Exception) {
                // Fails gracefully; simply keeps the previous quote on screen
                e.printStackTrace()
            }
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

        // Pillar 2: Dynamic Live Quote Overlay
        Crossfade(
            targetState = currentQuote,
            animationSpec = tween(durationMillis = 2000),
            label = "QuoteCrossfade",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 48.dp, end = 48.dp)
        ) { quoteData ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\"${quoteData.quoteText}\"",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "— ${quoteData.author}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}