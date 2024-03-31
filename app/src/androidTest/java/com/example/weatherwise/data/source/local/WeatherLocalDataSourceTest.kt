package com.example.weatherwise.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherwise.dp.WeatherDao
import com.example.weatherwise.dp.WeatherDatabase
import com.example.weatherwise.dp.WeatherLocalDataSource
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.model.entities.Alert
import com.example.weatherwise.model.entities.FavoriteWeather
import com.example.weatherwise.model.entities.WeatherResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class WeatherLocalDataSourceTest {

    lateinit var database: WeatherDatabase
    lateinit var localDataSource: WeatherLocalDataSource
    lateinit var dao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        localDataSource = WeatherLocalDataSourceImpl(ApplicationProvider.getApplicationContext())
        dao = database.getWeatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getWeatherResponse_responseNamedWeatherResponse1_theSameResponse() = runTest{
        //Given
        val weatherResponse1 = WeatherResponse(2)
        localDataSource.insertWeatherResponse(weatherResponse1)

        //When
        val result = localDataSource.getWeatherResponse()

        //Then
        val job =launch {
            result.collect{
                assertThat(result, not(nullValue()))
                assertThat(result, `is`(weatherResponse1))
            }
        }

        job.cancel()

    }


    @Test
    fun getAlerts_alertNamedAlert1_theSameAlert() = runTest{
        //Given
        val alert1 = Alert(2)
        localDataSource.insertAlert(alert1)

        //When
        val result = localDataSource.getAllAlerts()

        //Then
        val job =launch {
            result.collect{
                assertThat(result, not(nullValue()))
                assertThat(result, `is`(alert1))
            }
        }

        job.cancel()

    }

    @Test
    fun getFavorite_favoriteNamedFavorite1_theSameFavorite() = runTest{
        //Given
        val favorite1 = FavoriteWeather(2)
        localDataSource.insertFavorite(favorite1)

        //When
        val result1 = localDataSource.getFavoriteById(favorite1.fav_id)
        val result2 = localDataSource.getAllFavorites()

        //Then
        val job =launch {
            result1.collect{
                assertThat(result1, not(nullValue()))
                assertThat(result1, `is`(favorite1))
            }

            result2.collect{
                assertThat(result2, not(nullValue()))
                assertThat(result2, `is`(favorite1))
            }
        }

        job.cancel()

    }


    @Test
    fun deleteAlert_alert_alertDeletedSuccessfully() = runTest {
        // Given
        val alert = Alert(1)
        dao.insertAlert(alert)

        // When
        localDataSource.deleteAlert(alert)

        // Then
        val result = localDataSource.getAllAlerts()
        val job = launch {
            result.collect {
                assertThat(it.isEmpty(), not(true))
                assertThat(it.contains(alert), not(true))
            }
        }
        job.cancel()
    }

    @Test
    fun deleteFavorite_favorite_favoriteDeletedSuccessfully() = runTest {
        // Given
        val favorite = FavoriteWeather(1)
        dao.insertFavorite(favorite)

        // When
        localDataSource.deleteFavorite(favorite)

        // Then
        val result = localDataSource.getAllFavorites()
        val job = launch {
            result.collect {
                assertThat(it.isEmpty(), not(true))
                assertThat(it.contains(favorite), not(true))
            }
        }
        job.cancel()
    }
}