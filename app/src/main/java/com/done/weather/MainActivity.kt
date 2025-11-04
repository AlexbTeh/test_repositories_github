package com.done.weather


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.done.designsystem.theme.AppTheme
import com.done.profile.AddPlayerFullScreen
import com.done.profile.CreateRoundScreen
import com.done.profile.CreateRoundViewModel
import com.done.repositories.ScoreboardRoute
import com.done.weather.navigation.ROUTE_ADD_PLAYER
import com.done.weather.navigation.ROUTE_CREATE
import com.done.weather.navigation.ROUTE_SCOREBOARD
import com.done.weather.navigation.openAddPlayer
import com.done.weather.navigation.openScoreboard
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
    val navController = rememberNavController()
    NavHost(navController, startDestination = ROUTE_CREATE) {
        composable(ROUTE_CREATE) { backStackEntry ->
            val vm: CreateRoundViewModel = hiltViewModel(backStackEntry)

            val saved = backStackEntry.savedStateHandle
            val incomingPlayer by remember(saved) {
                saved.getStateFlow("player", null as com.done.domain.models.Player?)
            }.collectAsStateWithLifecycle()

            LaunchedEffect(incomingPlayer) {
                incomingPlayer?.let { p ->
                    vm.addPlayer(p)
                    saved.remove<com.done.domain.models.Player>("player")
                }
            }

            CreateRoundScreen(
                onAddPlayer = { navController.openAddPlayer() },
                onStartRound = { id -> navController.openScoreboard(id) },
                vm = vm
            )
        }

        composable(ROUTE_ADD_PLAYER) {
            AddPlayerFullScreen(
                onClose = { navController.popBackStack() },
                onAdded = { player ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("player", player)
                    navController.popBackStack()
                }
            )
        }

        composable(ROUTE_SCOREBOARD) {
            val prevEntry = navController.previousBackStackEntry
            val roundId = remember(prevEntry) {
                prevEntry?.savedStateHandle?.get<String>("roundId")
            } ?: ""

            LaunchedEffect(Unit) {
                prevEntry?.savedStateHandle?.remove<String>("roundId")
            }

            ScoreboardRoute(
                roundId = roundId,
                onBack = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack(ROUTE_CREATE, inclusive = false)}
            )
        }


    }
}
