package com.teamrocket.conductorapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ConductorApiService {
    @POST("/api/conductor/login")
    suspend fun sendOtp(@Body request: AuthRequest): AuthResponse
    
    @POST("/api/conductor/verify")
    suspend fun verifyOtp(@Body request: VerifyRequest): VerifyResponse
    
    @POST("/api/conductor/profile")
    suspend fun updateProfile(@Body request: ProfileRequest): ProfileResponse
    
    @POST("/api/bus/crowd")
    suspend fun updateCrowd(@Body request: CrowdRequest): CrowdResponse
    
    @POST("/api/ticket/verify")
    suspend fun verifyTicket(@Body request: TicketRequest): TicketResponse

    @retrofit2.http.GET("/api/bus/{busId}")
    suspend fun getBusDetails(@retrofit2.http.Path("busId") busId: String): BusDetailsResponse

    @retrofit2.http.GET("/api/routes")
    suspend fun getRoutes(): RouteResponse

    @retrofit2.http.GET("/api/buses/all")
    suspend fun getBuses(): BusListResponse
}

data class AuthRequest(val mobile: String)
data class AuthResponse(val success: Boolean, val message: String, val otp: String?)

data class VerifyRequest(val mobile: String, val otp: String)
data class VerifyResponse(val success: Boolean, val message: String, val conductor: ConductorProfile?)

data class ProfileRequest(val mobile: String, val name: String, val age: Int?, val gender: String?, val address: String?)
data class ProfileResponse(val success: Boolean, val conductor: ConductorProfile?)

data class ConductorProfile(
    val _id: String,
    val mobile: String,
    val name: String?,
    val isVerified: Boolean
)

data class CrowdRequest(
    val busId: String, 
    val crowdLevel: String? = null, 
    val passengerCount: Int? = null,
    val maleCount: Int? = null,
    val femaleCount: Int? = null
)
data class CrowdResponse(val success: Boolean, val message: String)

data class TicketRequest(val ticketId: String, val busId: String)
data class TicketResponse(val success: Boolean, val valid: Boolean, val message: String)

data class BusDetailsResponse(val success: Boolean, val bus: BusData?)
data class BusData(
    val busId: String,
    val crowdLevel: String,
    val currentPassengers: Int,
    val maleCount: Int = 0,
    val femaleCount: Int = 0,
    val lat: Double,
    val lng: Double
)

data class RouteResponse(val success: Boolean, val routes: List<RouteInfo>)
data class RouteInfo(
    val routeId: String,
    val name: String,
    val source: String,
    val destination: String,
    val checkpoints: List<Checkpoint>
)

data class Checkpoint(
    val name: String?,
    val lat: Double,
    val lng: Double
)

data class BusListResponse(val success: Boolean, val buses: List<String>)

object RetrofitClient {
    // Live Vercel Server
    private const val BASE_URL = "https://go-saathi.vercel.app/" 

    val apiService: ConductorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConductorApiService::class.java)
    }
}
