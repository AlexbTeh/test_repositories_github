package com.done.weather


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.done.designsystem.theme.AppTheme
import com.done.profile.CreateRoundScreen
import com.done.repositories.ScoreboardRoute
import com.done.weather.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppTheme { AppNav() } }
    }
}

@Composable
private fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.CREATE) {
        composable(Routes.CREATE) {
            CreateRoundScreen(
                onClose = { nav.popBackStack() },
                onStart = { roundId ->
                    nav.navigate("${Routes.SCORECARD}/$roundId")
                })
        }
        composable(
            route = "${Routes.SCORECARD}/{roundId}",
            arguments = listOf(navArgument("roundId") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("roundId").orEmpty()
            ScoreboardRoute(
                onBack = { nav.popBackStack() },
                roundId = id
            )
        }
    }
}
