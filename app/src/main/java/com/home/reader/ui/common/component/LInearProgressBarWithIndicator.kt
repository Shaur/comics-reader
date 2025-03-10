package com.home.reader.ui.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LinearProgressBarWithIndicator(
    current: Int,
    last: Int
) {

    val width = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp
    }

    val percentage = current / last.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = "${current}/${last}",
            fontSize = 14.sp,
            lineHeight = 15.sp,
            style = TextStyle(fontSize = 14.sp),
            color = Color.White,
            modifier = Modifier
                .offset(x = width * percentage - (50.dp * percentage))

        )
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        LinearProgressBarWithIndicator(100, 100)
    }
}