package com.home.reader.ui.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.util.Supplier
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun ProgressAsyncImage(
    model: Supplier<ImageRequest>,
    contentDescription: String?,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    var retryCounter by remember { mutableIntStateOf(0) }

    Box {
        key(retryCounter) {
            val painter = rememberAsyncImagePainter(
                model = model.get(),
                filterQuality = FilterQuality.High
            )

            val state = painter.state

            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )

            if (state is AsyncImagePainter.State.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (state is AsyncImagePainter.State.Error) {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Button(
                        onClick = { retryCounter = retryCounter + 1 }
                    ) {
                        Text("Refresh")
                    }
                }
            }
        }

    }
}

@Preview
@Composable
private fun Preview() {
    var retryCounter by remember { mutableIntStateOf(0) }

    Box {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = { retryCounter = retryCounter + 1 }
            ) {
                Text("Refresh")
            }
        }
    }
}