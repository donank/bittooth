package com.donank.bittooth.BTHelper

interface MessageConstants {
    val MESSAGE_STATE_CHANGE: Int
        get() = 0
    val MESSAGE_READ: Int
        get() = 2
    val MESSAGE_WRITE: Int
        get() = 3
    val MESSAGE_DEVICE_NAME: Int
        get() = 4
    val MESSAGE_TOAST: Int
        get() = 5
    val DEVICE_NAME: String
        get() = "device_name"
    val TOAST: String
        get() = "toast"
}