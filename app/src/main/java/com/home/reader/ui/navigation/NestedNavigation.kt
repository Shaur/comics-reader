package com.home.reader.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.home.reader.ui.issues.screen.IssuesScreen
import com.home.reader.ui.series.screen.SeriesScreen
import com.home.reader.ui.reader.screen.ReaderScreen
import com.home.reader.utils.Constants.Argument
import com.home.reader.utils.Constants.ArgumentsPlaceholder

fun NavGraphBuilder.authenticatedGraph(controller: NavController) {

    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Series.route
    ) {
        composable(route = NavigationRoutes.Authenticated.Series.route) {
            SeriesScreen(
                onNavigateToIssuesScreen = { id, name ->
                    controller.navigate(route = NavigationRoutes.Authenticated.Issues.route + "/$id/$name")
                }
            )
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
            IssuesScreen(
                seriesId = seriesId,
                seriesName = seriesName,
                onNavigateToReaderScreen = { id, currentPage, lastPage ->
                    controller.navigate(route = NavigationRoutes.Authenticated.Reader.route + "/$id/$currentPage/$lastPage")
                }
            )
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
    }

}