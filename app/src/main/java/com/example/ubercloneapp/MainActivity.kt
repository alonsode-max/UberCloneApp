package com.example.ubercloneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ubercloneapp.navigation.AppNavigation
import com.example.ubercloneapp.ui.theme.UberCloneTheme
import com.example.ubercloneapp.viewmodel.AuthViewModel
import com.example.ubercloneapp.viewmodel.PaymentViewModel
import com.example.ubercloneapp.viewmodel.ProfileViewModel
import com.example.ubercloneapp.viewmodel.RideViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UberCloneTheme() {
                val navController = rememberNavController()

                val authVm: AuthViewModel = hiltViewModel()

                val rideVm: RideViewModel = hiltViewModel()

                val paymentVm: PaymentViewModel = viewModel()

                val profileVm: ProfileViewModel = viewModel()



                AppNavigation(
                    navController = navController,
                    authVm        = authVm,
                    rideVm        = rideVm,
                    paymentVm = paymentVm,
                    profileVm = profileVm
                )
            }
        }
    }
}