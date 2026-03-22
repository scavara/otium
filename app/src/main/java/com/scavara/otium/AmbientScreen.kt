package com.scavara.otium

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AmbientScreen() {
    val context = LocalContext.current
    val imageLoader = context.imageLoader

    // 1. Initialize our Settings Repository and Coroutine Scope
    val settingsRepo = remember { SettingsRepository(context) }
    val scope = rememberCoroutineScope()

    // 2. Read preferences from DataStore
    val showQuotes by settingsRepo.showQuotesFlow.collectAsState(initial = true)
    val currentSize by settingsRepo.quoteSizeFlow.collectAsState(initial = QuoteSize.MEDIUM)
    val currentPosition by settingsRepo.quotePositionFlow.collectAsState(initial = QuotePosition.BOTTOM_START)

    // Audio Preferences
    val audioEnabled by settingsRepo.audioEnabledFlow.collectAsState(initial = false)
    val currentSoundscape by settingsRepo.soundscapeFlow.collectAsState(initial = Soundscape.RAIN)

    var showOptions by remember { mutableStateOf(false) }

    // Content States
    var currentQuote by remember { mutableStateOf("Take a deep breath...") }
    var currentImageUrl by remember { mutableStateOf("") }
    var nextQuote by remember { mutableStateOf<String?>(null) }
    var nextImageUrl by remember { mutableStateOf<String?>(null) }
    var tick by remember { mutableIntStateOf(0) }

    // Initialize ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE // Crucial for gapless looping
        }
    }

    // Audio Engine Lifecycle & Track Management
    LaunchedEffect(audioEnabled, currentSoundscape) {
        if (audioEnabled) {
            val audioRes = when(currentSoundscape) {
                Soundscape.RAIN -> R.raw.rain
                Soundscape.WAVES -> R.raw.waves
                Soundscape.FOREST -> R.raw.forest
                Soundscape.NOISE -> R.raw.white_noise
            }
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/$audioRes")

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Background Image & Quote Cycling
    LaunchedEffect(tick) {
        if (tick == 0) {
            try {
                currentImageUrl = UnsplashApi.service.getRandomNatureImage().urls.regular
                currentQuote = QuoteApi.service.getRandomQuote().formattedQuote
            } catch (e: Exception) { e.printStackTrace() }
        } else {
            if (nextImageUrl != null) currentImageUrl = nextImageUrl as String
            if (nextQuote != null) currentQuote = nextQuote as String
        }

        try {
            val fetchedNextUrl = UnsplashApi.service.getRandomNatureImage().urls.regular
            val fetchedNextQuote = QuoteApi.service.getRandomQuote().formattedQuote

            nextImageUrl = fetchedNextUrl
            nextQuote = fetchedNextQuote

            imageLoader.enqueue(ImageRequest.Builder(context).data(fetchedNextUrl).build())
        } catch (e: Exception) { e.printStackTrace() }

        delay(30_000L)
        tick++
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = tick,
            animationSpec = tween(durationMillis = 1500),
            label = "ambient_crossfade"
        ) { targetTick ->
            key(targetTick) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (currentImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(currentImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Ambient Nature Background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                }
            }
        }

        if (showQuotes) {
            // 3. Map DataStore enum to actual Compose Alignment
            val alignment = when (currentPosition) {
                QuotePosition.TOP_START -> Alignment.TopStart
                QuotePosition.CENTER -> Alignment.Center
                QuotePosition.BOTTOM_START -> Alignment.BottomStart
            }

            // 4. Map DataStore enum to actual Compose Typography sizes
            val textStyle = when (currentSize) {
                QuoteSize.SMALL -> MaterialTheme.typography.titleMedium
                QuoteSize.MEDIUM -> MaterialTheme.typography.headlineMedium
                QuoteSize.LARGE -> MaterialTheme.typography.displayMedium
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(58.dp),
                contentAlignment = alignment // Apply dynamic alignment
            ) {
                Text(
                    text = currentQuote,
                    color = Color.White,
                    style = textStyle // Apply dynamic text size
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { showOptions = true }) {
                Icon(Icons.Default.Settings, "Options Menu", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }

    if (showOptions) {
        Dialog(onDismissRequest = { showOptions = false }) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.85f), shape = MaterialTheme.shapes.large)
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Otium Settings", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(32.dp))

                    // Audio Toggle
                    Button(onClick = { scope.launch { settingsRepo.toggleAudio(audioEnabled) } }) {
                        Text(if (audioEnabled) "Audio: ON" else "Audio: OFF")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Soundscape Cycle Button
                    Button(onClick = {
                        scope.launch {
                            val nextTrack = when(currentSoundscape) {
                                Soundscape.RAIN -> Soundscape.WAVES
                                Soundscape.WAVES -> Soundscape.FOREST
                                Soundscape.FOREST -> Soundscape.NOISE
                                Soundscape.NOISE -> Soundscape.RAIN
                            }
                            settingsRepo.setSoundscape(nextTrack)
                        }
                    }) {
                        Text("Soundscape: ${currentSoundscape.name}")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle Quotes Button
                    Button(onClick = { scope.launch { settingsRepo.toggleShowQuotes(showQuotes) } }) {
                        Text(if (showQuotes) "Quotes: ON" else "Quotes: OFF")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Cycle Text Size Button
                    Button(onClick = {
                        scope.launch {
                            val nextSize = when(currentSize) {
                                QuoteSize.SMALL -> QuoteSize.MEDIUM
                                QuoteSize.MEDIUM -> QuoteSize.LARGE
                                QuoteSize.LARGE -> QuoteSize.SMALL
                            }
                            settingsRepo.setQuoteSize(nextSize)
                        }
                    }) {
                        Text("Size: ${currentSize.name}")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Cycle Position Button
                    Button(onClick = {
                        scope.launch {
                            val nextPos = when(currentPosition) {
                                QuotePosition.BOTTOM_START -> QuotePosition.CENTER
                                QuotePosition.CENTER -> QuotePosition.TOP_START
                                QuotePosition.TOP_START -> QuotePosition.BOTTOM_START
                            }
                            settingsRepo.setQuotePosition(nextPos)
                        }
                    }) {
                        Text("Position: ${currentPosition.name.replace("_", " ")}")
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(onClick = { showOptions = false }) {
                        Text("Close")
                    }
                }
            }
        }
    }
}