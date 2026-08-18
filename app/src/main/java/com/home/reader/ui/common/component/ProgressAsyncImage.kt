package com.home.reader.ui.common.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.zIndex
import androidx.core.util.Supplier
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.mlkit.vision.text.TextRecognizer
import com.home.reader.utils.RecognizedBlock
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
    onLoadError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var positions by remember { mutableStateOf(listOf<RecognizedBlock>()) }
    val graphicsLayer = rememberGraphicsLayer()

    val scope = rememberCoroutineScope()

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val painter = rememberAsyncImagePainter(
        model = model.get(),
        filterQuality = FilterQuality.High
    )

    var recognize by remember { mutableStateOf(false) }

    LaunchedEffect(verticalScrollState.value, horizontalScrollState.value) {
        snapshotFlow { graphicsLayer }
            .debounce(300.milliseconds)
            .collect { layer ->
                scope.launch {
                    recognizer.process(layer) {
                        positions = it
                        recognize = false
                    }
                }
            }
    }

    LaunchedEffect(painter.state) {
        if (painter.state !is AsyncImagePainter.State.Success) {
            return@LaunchedEffect
        }

        snapshotFlow { graphicsLayer }
            .debounce(300.milliseconds)
            .collect { layer ->
                scope.launch {
                    recognizer.process(layer) {
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

        val ctx = LocalContext.current

        for (block in positions) {
            val relativeBounds = with(LocalDensity.current) {
                RelativeRect(
                    x = block.rect.left.toDp(),
                    y = block.rect.top.toDp(),
                    width = block.rect.width().toDp(),
                    height = block.rect.height().toDp()
                )
            }

            CompositionLocalProvider(
                LocalTextToolbar provides SelectedTextToolbar(ctx, LocalClipboard.current)
            ) {
                SelectionContainer(
                    modifier = Modifier
                        .width(relativeBounds.width)
                        .height(relativeBounds.height)
                        .offset(relativeBounds.x, relativeBounds.y)
                        .background(color = Color.Transparent)
                ) {
                    Column {
                        val lineHeight = relativeBounds.height / block.data.size
                        for (data in block.data) {
                            RecognizedText(data, relativeBounds.x, lineHeight)
                        }
                    }
                }
            }

        }


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
                onRefreshClick = onLoadError
            )
        }
    }
}
