package com.teamrocket.passengerapp.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

data class UserRequest(
    val name: String,
    val mobile: String,
    val gender: String,
    val age: Int?,
    val address: String?
)

data class User(
    val _id: String,
    val name: String,
    val mobile: String,
    val gender: String,
    val age: Int?,
    val address: String?
)

data class UserResponse(
    val success: Boolean,
    val user: User?
)

data class RegistrationResponse(
    val success: Boolean,
    val message: String
)

data class Bus(
    val busId: String,
    val lat: Double,
    val lng: Double,
    val speed: Double,
    val crowdLevel: String = "Low",
    val currentPassengers: Int = 0,
    val femaleCount: Int = 0,
    val routeId: String?,
    val lastUpdated: String
)

data class BusListResponse(
    val success: Boolean,
    val buses: List<Bus>
)

interface ApiService {
    @POST("/api/register")
    suspend fun registerUser(@Body user: UserRequest): RegistrationResponse

    @GET("/api/user/{mobile}")
    suspend fun getUser(@Path("mobile") mobile: String): UserResponse

    @PUT("/api/user/{mobile}")
    suspend fun updateUser(@Path("mobile") mobile: String, @Body user: UserRequest): RegistrationResponse

    @GET("/api/buses")
    suspend fun getBuses(): BusListResponse

    @GET("/api/bus-stands")
    suspend fun getBusStands(): BusStandListResponse

    @GET("/api/routes")
    suspend fun getRoutes(): RouteResponse

    @POST("/api/routes/suggest")
    suspend fun getSmartRoutes(@Body request: SmartRouteRequest): SuggestionResponse

    @POST("/api/ticket/book")
    suspend fun bookTicket(@Body request: BookTicketRequest): BookTicketResponse
}

data class BookTicketRequest(val busId: String, val destination: String)
data class BookTicketResponse(val success: Boolean, val message: String)

data class SmartRouteRequest(val origin: LocationPoint, val dest: LocationPoint)
data class LocationPoint(val lat: Double, val lng: Double)

data class SuggestionResponse(
    val success: Boolean,
    val routes: List<RouteSuggestion>,
    val message: String?
)

data class RouteSuggestion(
    val busId: String,
    val totalScore: Int,
    val crowdLevel: String, 
    val duration: String,
    val eta: String,
    val speed: Double,
    val currentPassengers: Int = 0,
    val femaleCount: Int = 0,
    val badges: List<String>,
    val isBestChoice: Boolean = false
)

data class BusStand(
    val name: String,
    val lat: Double,
    val lng: Double,
    val isDepot: Boolean
)

data class BusStandListResponse(
    val success: Boolean,
    val stands: List<BusStand>
)

data class RoutePoint(
    val lat: Double,
    val lng: Double
)

data class RouteResponse(
    val success: Boolean,
    val points: List<RoutePoint>
)
