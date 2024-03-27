package com.example.weatherwise.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherwise.dp.WeatherDao
import com.example.weatherwise.dp.WeatherDatabase
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@SmallTest
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    lateinit var dao: WeatherDao
    lateinit var database: WeatherDatabase


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()

        dao = database.getWeatherDao()

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getWeatherResponse_weatherResponseId1_theSameWeatherResponse() = runTest {


        //Given
        val weatherResponse = WeatherResponse(id = 1)
        dao.insertWeatherResponse(weatherResponse)

        //When
        val result = dao.getWeatherResponse()

        //Then
        val job = launch {
            result.collect {
                assertThat(result, not(CoreMatchers.nullValue()))
                assertThat(result, `is`(weatherResponse))

            }
        }

        job.cancel()

    }

    @Test
    fun getAlerts_alertId1_theSameAlert() = runTest {


        // Given
        val alert = Alert(id = 1)
        dao.insertAlert(alert)

        // When
        val result = dao.getAlerts()


        // Then
        val job = launch {
            result.collect {
                assertThat(result, not(nullValue()))
                assertThat(result, `is`(1))
                assertThat(result, `is`(alert))
            }
        }
        job.cancel()
    }

    @Test
    fun getFavorites_favoriteId1_theSameFavorite() = runTest {


        // Given
        val favorite = FavoriteWeather(fav_id = 1)
        dao.insertFavorite(favorite)

        // When
        val result = dao.getFavoriteById(favorite.fav_id)
        val result2 = dao.getAllFavorites()


        // Then
        val job = launch {
            result.collect {
                //get favorite by id
                assertThat(result, not(nullValue()))
                assertThat(result, `is`(1))
                assertThat(result, `is`(favorite))

                //get favorite
                assertThat(result2, not(CoreMatchers.nullValue()))
                assertThat(result2, `is`(1))
                assertThat(result2, `is`(favorite))
            }
        }
        job.cancel()
    }
}