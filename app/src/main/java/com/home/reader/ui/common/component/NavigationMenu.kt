package com.home.reader.ui.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.home.reader.persistence.entity.User
import com.home.reader.ui.navigation.NavigationRoutes

@Composable
fun NavigationMenu(
    loginState: MutableState<User?>,
    controller: NavController,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.AccountCircle, contentDescription = "Account") },
                    label = {
                        if (loginState.value != null) {
                            Text(text = loginState.value?.username ?: "")
                        } else {
                            Text(text = "Log in")
                        }
                    },
                    selected = false,
                    onClick = { if (loginState.value == null) controller.navigate(NavigationRoutes.Unauthenticated.Login) }
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "My shelf") },
                    selected = false,
                    onClick = { controller.navigate(NavigationRoutes.Authenticated.Series.route) }
                )

                if (loginState.value != null) {
                    NavigationDrawerItem(
                        label = { Text(text = "Catalogue") },
                        selected = false,
                        onClick = { controller.navigate(NavigationRoutes.Authenticated.Catalogue.route) }
                    )
                }
            }
        },
        content = content
    )
}
