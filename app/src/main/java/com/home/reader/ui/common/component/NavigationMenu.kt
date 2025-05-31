package com.home.reader.ui.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.home.reader.persistence.entity.User
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.viewmodel.NavigationMenuViewModel
import com.home.reader.ui.navigation.NavigationRoutes

@Composable
fun NavigationMenu(
    viewModel: NavigationMenuViewModel = viewModel(factory = AppViewModelProvider.Factory),
    loginState: MutableState<User?>,
    controller: NavController,
    currentDestination: MutableState<String>,
    content: @Composable () -> Unit
) {
    loginState.value = viewModel.loginState.value

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                icon = { Icon(Icons.Rounded.AccountCircle, contentDescription = "Account") },
                label = {
                    if (loginState.value != null) {
                        Text(text = loginState.value?.username ?: "")
                    } else {
                        Text(text = "Log in")
                    }
                },
                selected = currentDestination.value == "login",
                onClick = {
                    currentDestination.value = "login"
                    if (loginState.value == null) controller.navigate(NavigationRoutes.Unauthenticated.Login)
                }
            )

            item(
                icon = { Icon(Icons.Rounded.Home, contentDescription = "My shelf") },
                label = { Text(text = "My shelf") },
                selected = currentDestination.value == "shelf",
                onClick = {
                    currentDestination.value = "shelf"
                    controller.navigate(NavigationRoutes.Authenticated.Series)
                }
            )

            if (loginState.value != null) {
                item(
                    icon = { Icon(Icons.Rounded.ShoppingCart, contentDescription = "Catalogue") },
                    label = { Text(text = "Catalogue") },
                    selected = currentDestination.value == "catalogue",
                    onClick = {
                        currentDestination.value = "catalogue"
                        controller.navigate(NavigationRoutes.Authenticated.Catalogue)
                    }
                )

                item(
                    icon = { Icon(Icons.Rounded.Close, contentDescription = "Log out") },
                    label = { Text(text = "Log out") },
                    selected = false,
                    onClick = { viewModel.logout() }
                )
            }
        },
        content = content
    )
}
