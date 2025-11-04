package com.done.weather.navigation

import androidx.navigation.NavController

const val ROUTE_CREATE = "create_round"
const val ROUTE_ADD_PLAYER = "add_player"
const val ROUTE_SCOREBOARD = "scoreboard"

fun NavController.openAddPlayer() = navigate(ROUTE_ADD_PLAYER)
fun NavController.openScoreboard(roundId: String) {
    currentBackStackEntry?.savedStateHandle?.set("roundId", roundId)
    navigate(ROUTE_SCOREBOARD)
}