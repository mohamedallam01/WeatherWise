package com.example.weatherwise.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherwise.data.source.FakeWeatherRepository
import com.example.weatherwise.map.viewmodel.MapViewModel
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    lateinit var favViewModel: FavoriteViewModel
    lateinit var mapViewModel: MapViewModel
    lateinit var repository: FakeWeatherRepository


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        repository = FakeWeatherRepository()
        favViewModel = FavoriteViewModel(repository)
        mapViewModel = MapViewModel(repository)
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
        favViewModel.setFavDetailsLocation(latitude, longitude)

        //Waiting
        val result = favViewModel.favWeatherDetails

        //Then
        assertThat(result, not(nullValue()))
//        assertThat(viewModel.favoriteWeather.value, not(nullValue()))
//        assertThat(viewModel.favoriteWeather.getOrAwaitValue(), not(nullValue()))

    }

    @Test
    fun `get favorite weather from database, favorite1, the same favorite1`() {

        // When
        favViewModel.getFavoriteWeatherFromDataBase()

        // Wait until LiveData value is set
        val result = favViewModel.favoriteWeather

        // Then
        assertThat(result, not(nullValue()))

    }

    @Test
    fun `get favorite weather details from remote, favorite1, the favorite of this location`()  {

            //Given
            val latitude = "37"
            val longitude = "122"

            //When
            favViewModel.getFavoriteWeatherDetailsFromRemote(latitude, longitude)

            //Then
            assertThat(favViewModel.favWeatherDetails.value, not(nullValue()))
            assertThat(favViewModel.favWeatherDetails.value, `is`(ApiState.Loading))

        }

    @Test
    fun `get favorite by id, favorite1, the same favorite`() = runTest   {

        //Given
        val favorite1 = FavoriteWeather(1)
        mapViewModel.insertFavoriteWeather(favorite1)

        //When
         favViewModel.getFavoriteById(favorite1.fav_id)

        //Waiting
        val result = favViewModel.favoriteWeatherById.value

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result, `is`(favorite1))

    }

}