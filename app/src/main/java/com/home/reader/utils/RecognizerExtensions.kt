package com.home.reader.utils

import android.graphics.Rect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.google.mlkit.vision.text.TextRecognizer

data class RecognizedData(
    val text: String,
    val lines: Int,
    val rect: Rect
)

private val ZERO_BLOCK = Rect(0, 0, 0, 0)

private fun error(exception: Exception): RecognizedData {
    return RecognizedData(
        text = exception.message ?: "Unknown error",
        lines = 1,
        rect = ZERO_BLOCK
    )
}

suspend fun TextRecognizer.process(
    graphicsLayer: GraphicsLayer,
    onResult: (List<RecognizedData>) -> Unit
) {
    if (graphicsLayer.size.width <= 0 || graphicsLayer.size.height <= 0) return

    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
    this.process(bitmap, 0)
        .addOnSuccessListener { text ->
            val positions = text.textBlocks
                .mapNotNull { block -> block.lines }
                .flatten()
                .mapNotNull { line ->
                    RecognizedData(
                        text = line.text.lowercase(),
                        lines = 1,
                        rect = line.boundingBox ?: ZERO_BLOCK
                    )
                }
            onResult(positions)
        }
        .addOnFailureListener { exception ->
            val positions = listOf(error(exception))
            onResult(positions)
        }
}