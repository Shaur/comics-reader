package com.home.reader.ui.profile

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.home.reader.component.dto.ProfileDto
import com.home.reader.ui.AppViewModelProvider

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
    profile: ProfileDto,
    navigateOnExit: () -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 50.dp)
    ) {
        Row(modifier = Modifier.height(40.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight().weight(2f)
            ) {
                Icon(Icons.Rounded.AccountCircle, contentDescription = "Account")
                Text(text = profile.username)
            }


            IconButton(
                onClick = {
                    viewModel.logout()
                    navigateOnExit()
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit")
            }
        }

    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = Color.WHITE.toLong()
)
private fun Preview() {
    val profile = ProfileDto(
        username = "Username"
    )

    MaterialTheme {
        ProfileScreen(profile = profile){}
    }
}