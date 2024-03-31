package com.example.weatherwise.alert.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherwise.data.source.FakeWeatherRepository
import com.example.weatherwise.model.entities.Alert
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AlertViewModelTest() {

    lateinit var viewModel: AlertViewModel
    lateinit var repository: FakeWeatherRepository

    val alert1 = Alert(1)


    @Before
    fun setUp() {
        repository = FakeWeatherRepository()
        viewModel = AlertViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `get All Alerts, alert1, the same alert`() = runTest {

        // Given an alert
        viewModel.insertAlert(alert1)

        // When getting all alerts
        viewModel.getAllAlerts()

        // Then
        val result = viewModel.allAlerts.value
        assertThat(result.size, `is`(1))
        assertThat(result.first(), `is`(alert1))

    }

    @Test
    fun `set Alert Location, set location, the alert of this location`() {

        //Given
        val latitude = "37"
        val longitude = "122"

        //When
        viewModel.setAlertLocation(latitude, longitude)

        //Then
        assertThat(viewModel.alertWeather, not(nullValue()))
        assertThat(viewModel.alertWeather.value, `is`(ApiState.Loading))

    }

    @Test
    fun `get Alert Weather, set location, the alert of this location`() {

        //Given
        val latitude = "37"
        val longitude = "122"

        //When
        viewModel.getAlertWeather(latitude, longitude)

        //Then
        assertThat(viewModel.alertWeather, not(nullValue()))
        assertThat(viewModel.alertWeather.value, `is`(ApiState.Loading))

    }


}