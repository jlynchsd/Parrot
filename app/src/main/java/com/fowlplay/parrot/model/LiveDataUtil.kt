package com.fowlplay.parrot.model

import android.os.Looper
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.update(value: T) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        this.value = value
    } else {
        this.postValue(value)
    }
}