package com.home.reader.ui.navigation

sealed class NavigationRoutes {

    sealed class Unauthenticated(val route: String): NavigationRoutes() {

        object NavigationRoute : Unauthenticated(route = "unauthenticated")

        object Login : Unauthenticated(route = "login")
    }

    sealed class Authenticated(val route: String): NavigationRoutes() {

        object NavigationRoute : Authenticated(route = "authenticated")

        object Series : Authenticated(route = "Series")

        object Issues : Authenticated(route = "Issues")

        object Reader : Authenticated(route = "Reader")
    }

}