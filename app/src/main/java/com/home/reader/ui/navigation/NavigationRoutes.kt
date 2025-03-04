package com.home.reader.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoutes {

    @Serializable
    sealed class Unauthenticated(val route: String): NavigationRoutes() {

        object NavigationRoute : Unauthenticated(route = "unauthenticated")

        @Serializable
        data object Login : Unauthenticated(route = "login")

    }

    @Serializable
    sealed class Authenticated(val route: String): NavigationRoutes() {

        object NavigationRoute : Authenticated(route = "authenticated")

        @Serializable
        object Series : Authenticated(route = "Series")

        @Serializable
        data class Issues(
            val seriesId: Long,
            val name: String
        )

        object Reader : Authenticated(route = "Reader")

        @Serializable
        object Catalogue : Authenticated(route = "Catalogue")
    }

}