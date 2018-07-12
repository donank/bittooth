package com.donank.bittooth.BTHelper

data class MessageConstants (
    val MESSAGE_STATE_CHANGE: Int = 0,
    val MESSAGE_READ: Int = 2,
    val MESSAGE_WRITE: Int = 3,
    val MESSAGE_DEVICE_NAME: Int = 4,
    val MESSAGE_TOAST: Int = 5,
    val DEVICE_NAME: String = "device_name",
    val TOAST: String = "toast"
)