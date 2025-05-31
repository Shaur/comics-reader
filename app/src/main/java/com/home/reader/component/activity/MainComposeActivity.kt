package com.home.reader.component.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.home.reader.async.IssuesUpdatesWorker
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

//        val workManager = WorkManager.getInstance(this)
//        val workRequest = OneTimeWorkRequestBuilder<IssuesUpdatesWorker>()
//            .setConstraints(Constraints(NetworkType.CONNECTED))
//            .build()
//
//        workManager.enqueue(workRequest)
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
    fun MainAppNavHost(navController: NavHostController = rememberNavController()) {
        val currentDestination = rememberSaveable { mutableStateOf("shelf") }
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.Authenticated.Series
        ) {
            authenticatedGraph(controller = navController, currentPosition = currentDestination)
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