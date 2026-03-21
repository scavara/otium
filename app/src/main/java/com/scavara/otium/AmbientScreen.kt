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
    val images = listOf(
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_compass
    )

    var currentIndex by remember { mutableIntStateOf(0) }

    // State to hold the live quote from Heroku
    var currentQuote by remember {
        mutableStateOf(QuoteResponse("Breathing in, I calm body and mind.", "Thich Nhat Hanh", ""))
    }

    // Timer: Fetches a new quote and cycles the image every 30 seconds
    LaunchedEffect(Unit) {
        while (true) {
            try {
                // Fetch from your Heroku API
                val nextQuote = QuoteApi.service.getRandomQuote()
                currentQuote = nextQuote
            } catch (e: Exception) {
                // If the network fails, it simply keeps showing the previous quote
                e.printStackTrace()
            }

            delay(30_000L)
            currentIndex = (currentIndex + 1) % images.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Pillar 1: Image Carousel (Unchanged)
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
            targetState = currentQuote, // Now crossfades based on the live object
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