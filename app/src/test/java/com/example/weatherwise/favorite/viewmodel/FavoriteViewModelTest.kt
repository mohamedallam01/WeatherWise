package com.example.weatherwise.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.getOrAwaitValue
import com.example.weatherwise.alert.viewmodel.AlertViewModel
import com.example.weatherwise.data.source.FakeWeatherRepository
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    lateinit var viewModel: FavoriteViewModel
    lateinit var repository: FakeWeatherRepository



    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        repository = FakeWeatherRepository()
        viewModel = FavoriteViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `set favorite details location, set location, the favorite of this location`() {

        //Given
        val latitude = "37"
        val longitude = "122"

        //When
        viewModel.setFavDetailsLocation(latitude, longitude)

        //Waiting
        val result = viewModel.favWeatherDetails

        //Then
        assertThat(result, not(nullValue()))
//        assertThat(viewModel.favoriteWeather.value, not(nullValue()))
//        assertThat(viewModel.favoriteWeather.getOrAwaitValue(), not(nullValue()))

    }

    @Test
    fun `get favorite weather from database, favorite1, the same favorite1`() {

        // When
        viewModel.getFavoriteWeatherFromDataBase()

        // Wait until LiveData value is set
        val result = viewModel.favoriteWeather

        // Then
        assertThat(result, not(nullValue()))

    }

    @Test
    fun `get favorite weather details from remote, favorite1, the favorite of this location`()  {

            //Given
            val latitude = "37"
            val longitude = "122"

            //When
            viewModel.getFavoriteWeatherDetailsFromRemote(latitude, longitude)

            //Then
            assertThat(viewModel.favWeatherDetails.value, not(nullValue()))
            assertThat(viewModel.favWeatherDetails.value, `is`(ApiState.Loading))

        }

    @Test
    fun `get favorite by id, favorite1, the same favorite`()  {

        //Given
        val favorite1 = FavoriteWeather(1)

        //When
         viewModel.getFavoriteById(favorite1.fav_id)

        //Waiting
        val result = viewModel.favoriteWeatherById

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result, `is`(favorite1.fav_id))

    }

}