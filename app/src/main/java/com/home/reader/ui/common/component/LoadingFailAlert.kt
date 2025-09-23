package com.home.reader.ui.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoadingFailAlert(
    onRefreshClick: () -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Button(
            onClick = onRefreshClick
        ) {
            Text("Refresh")
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Box {
        LoadingFailAlert(
            onRefreshClick = {},
            modifier = Modifier.align(Alignment.Center)
        )
    }
}