package com.example.myhabits3.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddEditViewModel : ViewModel() {

    private val colorPairLiveData: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val colorPair: LiveData<Pair<Int, Int>> get() = colorPairLiveData

    init {
        colorPairLiveData.value = null
    }

    fun setColorPair(curColor: Int, curColorNumber: Int) {
        colorPairLiveData.value = (Pair(curColor, curColorNumber))
    }

    fun clear() {
        colorPairLiveData.value = null
    }
}