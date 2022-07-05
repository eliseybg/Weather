package com.breaktime.weather.domain.repository

import com.breaktime.weather.domain.util.Resource
import com.breaktime.weather.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo>
}