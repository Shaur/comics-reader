package com.home.reader.ui.common.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.viewmodel.UpdateOnResumeViewModel

@Composable
fun UpdateOnResume(
    viewModel: UpdateOnResumeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onUpdate: (Long, Int, Boolean) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {viewModel.refresh(onUpdate)}
            Lifecycle.State.RESUMED -> {}
        }
    }
}