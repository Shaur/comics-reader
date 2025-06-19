package com.home.reader.ui.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.home.reader.component.dto.ProfileDto
import com.home.reader.persistence.entity.User
import com.home.reader.ui.catalogue.screen.CatalogueScreen
import com.home.reader.ui.common.component.NavigationMenu
import com.home.reader.ui.issues.screen.IssuesScreen
import com.home.reader.ui.login.screen.LoginScreen
import com.home.reader.ui.profile.ProfileScreen
import com.home.reader.ui.reader.configuration.ReaderConfig
import com.home.reader.ui.reader.screen.ReaderScreen
import com.home.reader.ui.series.screen.SeriesScreen

fun NavGraphBuilder.authenticatedGraph(
    controller: NavController,
    loginState: MutableState<User?> = mutableStateOf(null),
    currentPosition: MutableState<String>
) {
    composable<NavigationRoutes.Authenticated.Series> {
        NavigationMenu(
            loginState = loginState,
            controller = controller,
            currentDestination = currentPosition
        ) {
            SeriesScreen(
                loginState = loginState,
                onNavigateToIssuesScreen = { id, name ->
                    controller.navigate(NavigationRoutes.Authenticated.Issues(id, name))
                }
            )
        }
    }

    composable<NavigationRoutes.Authenticated.Issues> {
        val config: NavigationRoutes.Authenticated.Issues = it.toRoute()
        NavigationMenu(
            loginState = loginState,
            controller = controller,
            currentDestination = currentPosition
        ) {
            IssuesScreen(
                seriesId = config.seriesId,
                seriesName = config.name,
                onNavigateToReaderScreen = { config -> controller.navigate(config) }
            )
        }
    }

    composable<ReaderConfig> { ReaderScreen(config = it.toRoute()) }

    composable<NavigationRoutes.Unauthenticated.Login> {
        NavigationMenu(
            loginState = loginState,
            controller = controller,
            currentDestination = currentPosition
        ) {
            LoginScreen(navigateOnSuccess = { controller.navigate(NavigationRoutes.Authenticated.Series) })
        }
    }

    composable<NavigationRoutes.Authenticated.Catalogue> {
        NavigationMenu(
            loginState = loginState,
            controller = controller,
            currentDestination = currentPosition
        ) {
            CatalogueScreen(
                onNavigateToReaderScreen = { config -> controller.navigate(config) }
            )
        }
    }

    composable<NavigationRoutes.Authenticated.Profile> {
        NavigationMenu(
            loginState = loginState,
            controller = controller,
            currentDestination = currentPosition
        ) {
            ProfileScreen(
                profile = ProfileDto(username = loginState.value?.username ?: ""),
                navigateOnExit = { controller.navigate(NavigationRoutes.Authenticated.Series) }
            )
        }
    }
}