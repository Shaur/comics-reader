package com.home.reader.ui.common.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import androidx.core.util.Supplier
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.mlkit.vision.text.TextRecognizer
import com.home.reader.utils.RecognizedData
import com.home.reader.utils.process
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun ProgressAsyncImage(
    model: Supplier<ImageRequest>,
    contentDescription: String?,
    contentScale: ContentScale,
    recognizer: TextRecognizer,
    modifier: Modifier = Modifier
) {
    var retryCounter by remember { mutableIntStateOf(0) }
    var positions by remember { mutableStateOf(listOf<RecognizedData>()) }
    val graphicsLayer = rememberGraphicsLayer()

    val scope = rememberCoroutineScope()

    val painter = rememberAsyncImagePainter(
        model = model.get(),
        filterQuality = FilterQuality.High
    )

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var recognize by remember { mutableStateOf(false) }

    LaunchedEffect(graphicsLayer) {
        snapshotFlow { graphicsLayer }
            .debounce(300.milliseconds)
            .collect { layer ->

                scope.launch {
                    recognizer.process(graphicsLayer) {
                        positions = it
                        recognize = false
                    }
                }
            }
    }

    Box(
        modifier = Modifier.drawWithContent {
            graphicsLayer.record {
                this@drawWithContent.drawContent()
            }

            drawLayer(graphicsLayer)
        }
    ) {

        for (position in positions) {
            RecognizedText(position)
        }

        key(retryCounter) {
            val state = painter.state

            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
                    .zIndex(-1f)
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
            )

            if (state is AsyncImagePainter.State.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (state is AsyncImagePainter.State.Success) {
                Log.i("Recognition trigger", "Recognized: $recognize")
                recognize = true
            }

            if (state is AsyncImagePainter.State.Error) {
                LoadingFailAlert(
                    modifier = Modifier.align(Alignment.Center),
                    onRefreshClick = { retryCounter = retryCounter + 1 }
                )
            }
        }

    }
}
