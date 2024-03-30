package com.example.weatherwise.alert.view

import com.example.weatherwise.model.Alert

interface OnAlertClickListener {

    fun deleteAlert(alert: Alert)
}