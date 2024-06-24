package com.home.reader.ui.issues.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.BottomSheetButton
import com.home.reader.ui.common.component.RemoveDialog
import com.home.reader.ui.common.component.RenameDialog
import com.home.reader.ui.issues.component.IssueItem
import com.home.reader.ui.issues.viewmodel.IssuesViewModel
import com.home.reader.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuesScreen(
    viewModel: IssuesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    seriesId: Long,
    seriesName: String,
    onNavigateToReaderScreen: (id: Long, currentPage: Int, lastPage: Int) -> Unit
) {
    val state by remember { viewModel.state }

    var showRemoveDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var selectedIssueState by remember { mutableStateOf<Long?>(null) }
    val sheetState = rememberModalBottomSheetState()

    viewModel.refresh(seriesId)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = seriesName) },
                modifier = Modifier.background(Color.White)
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(Constants.Sizes.COVER_WIDTH),
            modifier = Modifier.padding(innerPadding)
        ) {
            this.items(state) {
                IssueItem(
                    issue = it,
                    onClick = onNavigateToReaderScreen,
                    onLongClock = { id ->
                        selectedIssueState = id
                    }
                )
            }
        }
    }

    if (selectedIssueState != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedIssueState = null },
            sheetState = sheetState
        ) {
            BottomSheetButton(icon = Icons.Sharp.Edit, capture = "Edit issue") {
                showRenameDialog = true
            }
            BottomSheetButton(icon = Icons.Sharp.Delete, capture = "Delete issue") {
                showRemoveDialog = true
            }
            BottomSheetButton(icon = Icons.Sharp.Check, capture = "Mark as read") {
                viewModel.markAsRead(selectedIssueState) {
                    selectedIssueState = null
                }

            }
        }
    }

    if (showRemoveDialog) {
        RemoveDialog(
            title = "Issue remove",
            onDismiss = {
                selectedIssueState = null
                showRemoveDialog = false
            },
            onConfirm = {
                selectedIssueState?.let {
                    viewModel.removeIssue(it)
                    viewModel.refresh(seriesId)
                }

                selectedIssueState = null
                showRemoveDialog = false
            }
        )
    }

    if (showRenameDialog) {
        RenameDialog(
            title = "Rename issue",
            baseText = state.first { it.id == selectedIssueState }.seriesName,
            onDismiss = {
                selectedIssueState = null
                showRenameDialog = false
            },
            onConfirm = { issueName ->
                selectedIssueState?.let {
                    viewModel.renameIssue(it, issueName)
                }

                selectedIssueState = null
                showRenameDialog = false
            }
        )
    }
}