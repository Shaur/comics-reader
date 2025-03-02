package com.home.reader.component.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.home.reader.ui.navigation.NavigationRoutes
import com.home.reader.ui.navigation.authenticatedGraph

class MainComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainApp()
            }
        }
    }

    @Composable
    fun MainApp() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainAppNavHost()
        }
    }

    @Composable
    fun MainAppNavHost(
//        modifier: Modifier = Modifier,
        navController: NavHostController = rememberNavController()
    ) {
        NavHost(
//            modifier = modifier,
            navController = navController,
            startDestination = NavigationRoutes.Authenticated.Series//NavigationRoute.route
        ) {
            authenticatedGraph(controller = navController)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MaterialTheme {
            MainApp()
        }
    }
}