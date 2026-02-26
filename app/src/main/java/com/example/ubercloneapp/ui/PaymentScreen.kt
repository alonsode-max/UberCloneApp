package com.example.ubercloneapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ubercloneapp.BuildConfig

import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

import com.example.ubercloneapp.viewmodel.PaymentState
import com.example.ubercloneapp.viewmodel.PaymentViewModel
@Composable
fun PaymentScreen(
    paymentVm:   PaymentViewModel,
    ridePrice:   Double,
    rideSummary: String,
    onPaymentOk: () -> Unit,
    onBack:      () -> Unit
) {
    val context = LocalContext.current
    val state = paymentVm.paymentState



    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
    }

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                paymentVm.onPaymentSuccess()
            }
            is PaymentSheetResult.Canceled -> {
                paymentVm.onPaymentCancelled()
            }
            is PaymentSheetResult.Failed -> {
                paymentVm.onPaymentFailed(result.error.message)
            }
        }
    }

    LaunchedEffect(state) {
        if (state is PaymentState.ReadyToPay) {
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = state.clientSecret,
                configuration = PaymentSheet.Configuration(
                    merchantDisplayName = "UberClone",
                )
            )
        }
        if (state is PaymentState.Success) {
            onPaymentOk()
        }
    }

    // ── UI ──
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("💳", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text("Pagar viaje",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text(rideSummary)
                Text("Total: ${ridePrice}€",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall)
            }
        }

        Spacer(Modifier.height(24.dp))

        when (state) {
            is PaymentState.Loading -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Preparando pago...")
            }
            is PaymentState.Error -> {
                Text(state.msg,
                    color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = {
                    paymentVm.createPaymentIntent(ridePrice, rideSummary)
                }) { Text("Reintentar pago") }
            }
            is PaymentState.Success -> {
                Text("✅ ¡Pago completado!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary)
            }
            else -> {
                // Idle o ReadyToPay
                Button(
                    onClick = {
                        paymentVm.createPaymentIntent(ridePrice, rideSummary)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("💳 Pagar ${ridePrice}€",
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) { Text("← Volver") }
    }
}