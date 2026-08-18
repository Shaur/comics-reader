package com.home.reader.utils

import android.graphics.Rect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer

data class RecognizedData(
    val text: String,
    val lines: Int,
    val rect: Rect
)

data class RecognizedBlock(
    val rect: Rect,
    val data: List<RecognizedData>
)

private val ZERO_BLOCK = Rect(0, 0, 0, 0)

private fun error(exception: Exception): RecognizedBlock {
    return RecognizedBlock(
        data = listOf(),
        rect = ZERO_BLOCK
    )
}

suspend fun TextRecognizer.process(
    graphicsLayer: GraphicsLayer,
    onResult: (List<RecognizedBlock>) -> Unit
) {
    if (graphicsLayer.size.width <= 0 || graphicsLayer.size.height <= 0) return

    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
    this.process(bitmap, 0)
        .addOnSuccessListener { text ->
            val positions = text.textBlocks
                .mapNotNull { block ->
                    val data = block.lines.mapNotNull(::convert)

                    RecognizedBlock(
                        data = data,
                        rect = block.boundingBox ?: ZERO_BLOCK
                    )
                }

            onResult(positions)
        }
        .addOnFailureListener { exception ->
            val positions = listOf(error(exception))
            onResult(positions)
        }
}

private fun convert(line: Text.Line): RecognizedData {
    return RecognizedData(
        text = line.text.uppercase(),
        lines = 1,
        rect = line.boundingBox ?: ZERO_BLOCK
    )
}