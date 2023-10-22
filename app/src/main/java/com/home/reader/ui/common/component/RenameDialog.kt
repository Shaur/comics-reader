package com.home.reader.ui.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RenameDialog(
    title: String,
    baseText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var text by remember { mutableStateOf(baseText) }

    AlertDialog(
        icon = { Icon(Icons.Rounded.Edit, contentDescription = title) },
        title = { Text(text = title) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Edit") }
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MaterialTheme {
        RenameDialog(
            title = "Edit something",
            baseText = "Shazam #3",
            onDismiss = { /*TODO*/ }
        ) {}
    }
}