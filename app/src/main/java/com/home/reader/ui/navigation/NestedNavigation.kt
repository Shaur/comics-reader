package com.home.reader.ui.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.home.reader.persistence.entity.User
import com.home.reader.ui.catalogue.CatalogueScreen
import com.home.reader.ui.common.component.NavigationMenu
import com.home.reader.ui.issues.screen.IssuesScreen
import com.home.reader.ui.login.screen.LoginScreen
import com.home.reader.ui.series.screen.SeriesScreen
import com.home.reader.ui.reader.screen.ReaderScreen
import com.home.reader.utils.Constants.Argument
import com.home.reader.utils.Constants.ArgumentsPlaceholder

fun NavGraphBuilder.authenticatedGraph(
    controller: NavController,
    loginState: MutableState<User?> = mutableStateOf(null)
) {

    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Series.route
    ) {
        composable(route = NavigationRoutes.Authenticated.Series.route) {
            NavigationMenu(loginState, controller) {
                SeriesScreen(
                    loginState = loginState,
                    onNavigateToIssuesScreen = { id, name ->
                        val normalized = name.replace("/", "\\")
                        controller.navigate(route = NavigationRoutes.Authenticated.Issues.route + "/$id/$normalized")
                    }
                )
            }
        }

        composable(
            route = NavigationRoutes.Authenticated.Issues.route + ArgumentsPlaceholder.SERIES_ID + ArgumentsPlaceholder.SERIES_NAME,
            arguments = listOf(
                navArgument(Argument.SERIES_ID) { type = NavType.LongType; nullable = false },
                navArgument(Argument.SERIES_NAME) { type = NavType.StringType; nullable = false }
            )
        ) {
            val seriesId = it.arguments?.getLong(Argument.SERIES_ID)!!
            val seriesName = it.arguments?.getString(Argument.SERIES_NAME, "")!!
            NavigationMenu(loginState, controller) {
                IssuesScreen(
                    seriesId = seriesId,
                    seriesName = seriesName,
                    onNavigateToReaderScreen = { id, currentPage, lastPage ->
                        controller.navigate(route = NavigationRoutes.Authenticated.Reader.route + "/$id/$currentPage/$lastPage")
                    }
                )
            }
        }

        composable(
            route = NavigationRoutes.Authenticated.Reader.route + ArgumentsPlaceholder.ISSUE_ID + ArgumentsPlaceholder.CURRENT_PAGE + ArgumentsPlaceholder.LAST_PAGE,
            arguments = listOf(
                navArgument(Argument.ISSUE_ID) { type = NavType.LongType; nullable = false },
                navArgument(Argument.CURRENT_PAGE) { type = NavType.IntType; nullable = false },
                navArgument(Argument.LAST_PAGE) { type = NavType.IntType; nullable = false }
            )
        ) {
            ReaderScreen(
                id = it.arguments?.getLong(Argument.ISSUE_ID)!!,
                currentPage = it.arguments?.getInt(Argument.CURRENT_PAGE)!!,
                lastPage = it.arguments?.getInt(Argument.LAST_PAGE)!!
            )
        }

        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
            NavigationMenu(loginState, controller) {
                LoginScreen(navigateOnSuccess = { controller.navigate(route = NavigationRoutes.Authenticated.Series.route) })
            }
        }

        composable(route = NavigationRoutes.Authenticated.Catalogue.route) {
            NavigationMenu(loginState, controller) {
                CatalogueScreen()
            }
        }
    }

}