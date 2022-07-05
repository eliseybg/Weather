package com.breaktime.weather.data.repository

import com.breaktime.weather.data.mappers.toWeatherInfo
import com.breaktime.weather.data.remote.WeatherApi
import com.breaktime.weather.domain.repository.WeatherRepository
import com.breaktime.weather.domain.util.Resource
import com.breaktime.weather.domain.weather.WeatherInfo
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo> {
        return try {
            val weatherData = weatherApi.getWeatherData(lat, long)
            val weatherInfo = weatherData.toWeatherInfo()
            Resource.Success(weatherInfo)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
}