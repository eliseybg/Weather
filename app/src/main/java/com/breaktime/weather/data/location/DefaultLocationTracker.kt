package com.breaktime.weather.data.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.breaktime.weather.domain.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {
    override suspend fun getCurrentLocation(): Location? {
        if (!isPermissionsGranted()) return null

        return suspendCancellableCoroutine { continuation ->
            locationProviderClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) continuation.resume(result)
                    else continuation.resume(null)
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener { continuation.resume(it) }
                addOnFailureListener { continuation.resume(null) }
                addOnCanceledListener { continuation.cancel() }
            }
        }
    }

    private fun isPermissionsGranted(): Boolean {
        val hasFineLocation = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocation = isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        val isGpsEnabled = isGpsEnabled()
        return hasFineLocation && hasCoarseLocation && isGpsEnabled
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            application, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isGpsEnabled(): Boolean {
        val service = application.getSystemService(Context.LOCATION_SERVICE)
        val locationManager = service as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}