package com.example.ubercloneapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class PaymentRequest(
    val amount:      Int,
    val currency:    String = "eur",
    val rideSummary: String = ""
)

data class PaymentIntentResponse(
    val clientSecret: String
)

interface StripeApiService {
    @POST("create-payment-intent")
    suspend fun createPaymentIntent(
        @Body request: PaymentRequest
    ): PaymentIntentResponse
}

object StripeClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://uber-clone-backend-mu-cyan.vercel.app/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: StripeApiService = retrofit.create(StripeApiService::class.java)
}