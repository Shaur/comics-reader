package com.home.reader.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoutes {

    @Serializable
    sealed class Unauthenticated(): NavigationRoutes() {

        @Serializable
        data object Login : Unauthenticated()

    }

    @Serializable
    sealed class Authenticated(): NavigationRoutes() {

        @Serializable
        object Series : Authenticated()

        @Serializable
        data class Issues(
            val seriesId: Long,
            val name: String
        )

        @Serializable
        object Catalogue : Authenticated()
    }

}