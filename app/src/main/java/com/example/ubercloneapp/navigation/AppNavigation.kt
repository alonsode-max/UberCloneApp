package com.example.ubercloneapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ubercloneapp.ui.*
import com.example.ubercloneapp.viewmodel.AuthViewModel
import com.example.ubercloneapp.viewmodel.PaymentViewModel
import com.example.ubercloneapp.viewmodel.ProfileViewModel
import com.example.ubercloneapp.viewmodel.RideViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authVm:        AuthViewModel,
    rideVm:        RideViewModel,
    paymentVm: PaymentViewModel,
    profileVm: ProfileViewModel
) {
    val startRoute = if (authVm.isLoggedIn) Routes.HOME_MAP else Routes.LOGIN

    NavHost(navController, startDestination = startRoute) {

        composable(Routes.LOGIN) {
            LoginScreen(
                authVm = authVm,
                onLoginOk = {
                    navController.navigate(Routes.HOME_MAP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authVm = authVm,
                onRegisterOk = {
                    navController.navigate(Routes.HOME_MAP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME_MAP) {
            HomeMapScreen(
                rideVm = rideVm,
                onRequestRide = {
                    navController.navigate(Routes.REQUEST)
                },
                onHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onLogout = {
                    authVm.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME_MAP) { inclusive = true }
                    }
                },
                onProfile={navController.navigate(Routes.PROFILE)}
            )
        }

        composable(Routes.REQUEST) {
            RequestRideScreen(
                rideVm = rideVm,
                onRideRequested = {
                    navController.navigate(Routes.IN_PROGRESS) {
                        popUpTo(Routes.REQUEST) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.IN_PROGRESS) {
            RideInProgressScreen(
                rideVm = rideVm,
                onCompleted = {
                    navController.navigate(Routes.PAYMENT) {
                        popUpTo(Routes.IN_PROGRESS) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PAYMENT) {
            PaymentScreen(
                paymentVm   = paymentVm,
                ridePrice   = rideVm.estimatePrice,
                rideSummary = "Mi ubicación → ${rideVm.destinationName}",
                onPaymentOk = {
                    rideVm.resetRide()
                    paymentVm.resetPayment()
                    navController.navigate(Routes.HOME_MAP) {
                        popUpTo(Routes.HOME_MAP) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            RideHistoryScreen(
                rideVm = rideVm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                profileVm = profileVm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}