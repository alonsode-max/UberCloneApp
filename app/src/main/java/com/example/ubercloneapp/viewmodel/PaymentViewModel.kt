package com.example.ubercloneapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ubercloneapp.network.PaymentRequest
import com.example.ubercloneapp.network.StripeClient
import kotlinx.coroutines.launch


sealed interface PaymentState {
    data object Idle    : PaymentState
    data object Loading : PaymentState

    data class ReadyToPay(
        val clientSecret: String
    ) : PaymentState

    data object Success : PaymentState
    data class  Error(val msg: String) : PaymentState
}

class PaymentViewModel : ViewModel() {

    var paymentState: PaymentState by mutableStateOf(PaymentState.Idle)
        private set

    fun createPaymentIntent(amountEuros: Double, rideSummary: String) {
        paymentState = PaymentState.Loading

        val amountCents = (amountEuros * 100).toInt()

        viewModelScope.launch {
            paymentState = try {
                val response = StripeClient.api.createPaymentIntent(
                    PaymentRequest(
                        amount = amountCents,
                        rideSummary = rideSummary
                    )
                )
                PaymentState.ReadyToPay(response.clientSecret)
            } catch (e: Exception) {
                PaymentState.Error(e.localizedMessage ?: "Error al crear pago")
            }
        }
    }
    fun onPaymentSuccess() { paymentState = PaymentState.Success }
    fun onPaymentFailed(msg: String?) {
        paymentState = PaymentState.Error(msg ?: "Pago fallido")
    }
    fun onPaymentCancelled() {
        paymentState = PaymentState.Idle
    }
    fun resetPayment() { paymentState = PaymentState.Idle }
}