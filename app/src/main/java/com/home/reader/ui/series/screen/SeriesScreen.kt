package com.home.reader.ui.series.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.BottomSheetButton
import com.home.reader.ui.common.component.RenameDialog
import com.home.reader.ui.series.component.SeriesItem
import com.home.reader.ui.series.viewmodel.SeriesViewModel
import com.home.reader.utils.Constants.COMICS_MIME_TYPES
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    viewModel: SeriesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToIssuesScreen: (id: Long, name: String) -> Unit
) {

    val state by remember { viewModel.state }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var selectedSeriesState by remember { mutableStateOf<Long?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val storagePermissionState = rememberPermissionState(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val chooseFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(), viewModel::loadIssues
    )

    viewModel.refresh()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!storagePermissionState.status.isGranted) {
                        storagePermissionState.launchPermissionRequest()
                    }

                    chooseFileLauncher.launch(COMICS_MIME_TYPES)
                },
                shape = CircleShape,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(10.dp)
            ) {
                Icon(Icons.Filled.Add, "Added new issue")
            }
        }
    ) { paddings ->

        Column {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(COVER_WIDTH),
                modifier = Modifier.padding(paddings)
            ) {
                items(state) {
                    SeriesItem(
                        series = it,
                        onClick = onNavigateToIssuesScreen,
                        onLongClick = { id -> selectedSeriesState = id }
                    )
                }
            }
        }
    }

    if (selectedSeriesState != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedSeriesState = null },
            sheetState = sheetState
        ) {
            BottomSheetButton(icon = Icons.Sharp.Edit, capture = "Edit series") {
                showRenameDialog = true
            }
            BottomSheetButton(icon = Icons.Sharp.Delete, capture = "Delete series") {
                showRemoveDialog = true
            }
        }
    }

    if (showRenameDialog) {
        RenameDialog(
            title = "Rename series",
            baseText = state.first { it.id == selectedSeriesState }.name,
            onDismiss = {
                selectedSeriesState = null
                showRenameDialog = false
            },
            onConfirm = { issueName ->
                selectedSeriesState?.let {
                    viewModel.renameSeries(it, issueName)
                }

                selectedSeriesState = null
                showRenameDialog = false
            }
        )
    }

}