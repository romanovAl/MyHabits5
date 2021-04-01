package com.example.myhabits3.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AddEditViewModel @Inject constructor() : ViewModel() {

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