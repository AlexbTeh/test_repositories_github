package com.done.weather.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.done.profile.navigateToProfileScreen
import com.done.profile.profileScreen
import com.done.repositories.repoListScreen
import com.done.repositories.repoListScreenRoute


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = repoListScreenRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        repoListScreen(onRepoItemClick = navController::navigateToProfileScreen)
        profileScreen(onBackBtnClick = navController::popBackStack)
    }
}