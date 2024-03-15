package com.example.weatherwise.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherwise.model.Alert

@Database([Alert::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getAlertDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WeatherDatabase::class.java, "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }


}