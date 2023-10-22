package com.home.reader.ui.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetButton(
    icon: ImageVector,
    capture: String,
    onClick: () -> Unit
) {

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black
        ),
        shape = RectangleShape,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = capture)
            Text(text = capture, fontSize = 15.sp, lineHeight = 16.sp)
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MaterialTheme {
        Column {
            BottomSheetButton(icon = Icons.Sharp.Delete, capture = "Delete something") {}
            BottomSheetButton(icon = Icons.Sharp.Edit, capture = "Edit something") {}
        }
    }
}