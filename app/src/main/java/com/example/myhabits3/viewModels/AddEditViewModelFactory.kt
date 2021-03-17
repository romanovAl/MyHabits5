package com.example.myhabits3.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddEditViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEditViewModel() as T
    }
}