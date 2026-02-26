package com.example.ubercloneapp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin")      origin:      String,  // "lat,lng"
        @Query("destination") destination: String,  // "lat,lng"
        @Query("key")         key:         String,  // API Key
        @Query("mode")        mode:        String = "driving"
    ): DirectionsResponse
}
data class DirectionsResponse(
    val routes: List<Route> = emptyList(),
    val status: String = ""
)

data class Route(
    val legs: List<Leg> = emptyList(),
    val overview_polyline: OverviewPolyline = OverviewPolyline()
)

data class Leg(
    val distance: TextValue = TextValue(),
    val duration: TextValue = TextValue(),
    val start_address: String = "",
    val end_address:   String = ""
)

data class TextValue(
    val text:  String = "",
    val value: Int    = 0
)

data class OverviewPolyline(
    val points: String = ""
)

