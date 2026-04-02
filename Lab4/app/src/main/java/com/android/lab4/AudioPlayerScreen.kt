package com.android.lab4

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun AudioPlayerScreen() {
    val context = LocalContext.current

    val defaultUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_audio}")
    var mediaTitle by remember { mutableStateOf("sample_audio.wav") }

    val player = remember {
        ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(defaultUri))
                prepare()
            }
    }

    DisposableEffect(player) {
        onDispose {
            player.pause()
            player.release()
        }
    }

    fun loadMedia(uri: Uri) {
        player.pause()
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
        val name = uri.lastPathSegment ?: uri.toString()
        mediaTitle = if (name.length > 40) name.takeLast(40) else name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        MediaSourceSelector(
            mimeType = "audio/*",
            onSourceSelected = { uri -> loadMedia(uri) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayerControls(
            player = player,
            title = mediaTitle
        )
    }
}
