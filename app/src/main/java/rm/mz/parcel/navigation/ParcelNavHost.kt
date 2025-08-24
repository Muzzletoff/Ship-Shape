package rm.mz.parcel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import rm.mz.parcel.ui.screens.auth.LoginScreen
import rm.mz.parcel.ui.screens.auth.RegisterScreen
import rm.mz.parcel.ui.screens.parcel.CreateParcelScreen
import rm.mz.parcel.ui.screens.parcel.ParcelDetailsScreen
import rm.mz.parcel.ui.screens.parcel.ParcelListScreen
import rm.mz.parcel.ui.screens.parcel.ParcelTrackingScreen
import rm.mz.parcel.ui.screens.profile.ProfileScreen
import rm.mz.parcel.ui.screens.settings.SettingsScreen
import rm.mz.parcel.ui.screens.help.HelpScreen
import rm.mz.parcel.ui.screens.home.HomeScreen
import rm.mz.parcel.ui.screens.parcel.SentParcelsScreen
import rm.mz.parcel.ui.screens.parcel.ReceivedParcelsScreen
import rm.mz.parcel.ui.screens.splash.SplashScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import rm.mz.parcel.util.AuthUtil
import rm.mz.parcel.ui.viewmodel.SplashViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ParcelNavHost(
    navController: NavHostController,
    startDestination: String,
    authUtil: AuthUtil = viewModel(),
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    val route = if (splashViewModel.isUserLoggedIn()) {
                        Screen.Home.route
                    } else {
                        Screen.Login.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(0)
                }}
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(0)
                }}
            )
        }
        
        composable(Screen.ParcelList.route) {
            ParcelListScreen(
                onParcelClick = { parcelId -> 
                    navController.navigate(Screen.ParcelDetails.createRoute(parcelId))
                },
                onCreateParcelClick = { 
                    navController.navigate(Screen.CreateParcel.route)
                },
                onLogout = {
                    authUtil.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                onProfileClick = { 
                    navController.navigate(Screen.Profile.route)
                },
                onSettingsClick = { 
                    navController.navigate(Screen.Settings.route)
                },
                onHelpClick = { 
                    navController.navigate(Screen.Help.route)
                }
            )
        }
        
        composable(
            route = Screen.ParcelDetails.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) {
            ParcelDetailsScreen(
                parcelId = it.arguments?.getString("parcelId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onTrackParcelClick = { parcelId ->
                    navController.navigate(Screen.ParcelTracking.createRoute(parcelId))
                }
            )
        }
        
        composable(
            route = Screen.ParcelTracking.route,
            arguments = listOf(navArgument("parcelId") { type = NavType.StringType })
        ) {
            ParcelTrackingScreen(
                parcelId = it.arguments?.getString("parcelId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.CreateParcel.route) {
            CreateParcelScreen(
                onNavigateBack = { navController.popBackStack() },
                onParcelCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onSentParcelsClick = { navController.navigate(Screen.SentParcels.route) },
                onReceivedParcelsClick = { navController.navigate(Screen.ReceivedParcels.route) },
                onProfileClick = { /* Already on profile */ },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onLogout = {
                    authUtil.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Help.route) {
            HelpScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onSentParcelsClick = { navController.navigate(Screen.SentParcels.route) },
                onReceivedParcelsClick = { navController.navigate(Screen.ReceivedParcels.route) },
                onCreateParcelClick = { navController.navigate(Screen.CreateParcel.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onLogout = {
                    authUtil.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.SentParcels.route) {
            SentParcelsScreen(
                onNavigateBack = { navController.popBackStack() },
                onParcelClick = { parcelId ->
                    navController.navigate(Screen.ParcelDetails.createRoute(parcelId))
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onLogout = {
                    authUtil.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.ReceivedParcels.route) {
            ReceivedParcelsScreen(
                onNavigateBack = { navController.popBackStack() },
                onParcelClick = { parcelId ->
                    navController.navigate(Screen.ParcelDetails.createRoute(parcelId))
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onLogout = {
                    authUtil.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
} 