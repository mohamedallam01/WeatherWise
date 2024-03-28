package com.example.weatherwise.data.source


import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class DefaultWeatherRepositoryTest {

    private val weather1 = WeatherResponse(1)
    private val weather2 = WeatherResponse(2)
    private val weather3 = WeatherResponse(3)
    private val weather4 = WeatherResponse(4)

    private val alert1 = Alert(1)
    private val favorite1 = FavoriteWeather(1)

    val localWeathers = mutableListOf<WeatherResponse>(
        weather1,weather2
    )

    val remoteWeathers = mutableListOf<WeatherResponse>(
        weather3,weather4
    )

    lateinit var fakeLocalDataSource: FakeLocalDataSource
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var repository: WeatherRepoImpl

    @Before
    fun setUp(){
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource(weather1)
        repository = WeatherRepoImpl.getInstance(fakeRemoteDataSource,fakeLocalDataSource)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getWeatherResponse localWeathers sameLocalWeathers`() = runBlockingTest {
        //Given done in the setup

        //when call get tasks and request updates
        val result = repository.getWeatherResponse()

        //Then result.data is local lists
        result.collect{
            assertThat(result, `is`(localWeathers))
        }


    }

    @Test
    fun `getCurrentWeatherFromRemote remoteWeathers sameRemoteWeathers`() = runTest {
        //Given done in the setup

        //when call get tasks and request updates
        val result = repository.getCurrentWeatherFromRemote()

        //Then result.data is remote
        val collectedResult = mutableListOf<WeatherResponse>()
        result.collect { weatherResponse ->
            collectedResult.add(weatherResponse)
        }

        assertThat(collectedResult.size, `is`(1))
        assertThat(collectedResult[0], `is`(weather1))


    }

    @Test
    fun `getAllAlerts alerts sameAlerts`() = runTest {
        //Given
        repository.insertAlert(alert1)

        //when call get tasks and request updates
        val result = repository.getAllAlerts()

        //Then result.data is remote
        result.collect {
            assertThat(result, `is`(1))
            assertThat(result, `is`(alert1))
        }




    }

    @Test
    fun `getAllFavorites favorites sameFavorites`() = runTest {
        //Given
        repository.insertFavorite(favorite1)

        //when call get tasks and request updates
        val result = repository.getAllFavorites()
        val result2 = repository.getFavoriteById(favorite1.fav_id)

        //Then result data is favorite
        result.collect {
            assertThat(result, `is`(1))
            assertThat(result, `is`(favorite1))

            assertThat(result2, `is`(1))
            assertThat(result2, `is`(favorite1.fav_id))
        }


    }
}