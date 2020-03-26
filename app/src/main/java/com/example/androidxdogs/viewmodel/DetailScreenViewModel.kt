package com.example.androidxdogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidxdogs.model.DogBreed
import com.example.androidxdogs.model.DogDatabase
import kotlinx.coroutines.launch

class DetailScreenViewModel(application: Application) : BaseViewModel(application) {

   val dogLiveData =  MutableLiveData<DogBreed>()

    fun fetch(uuid:Int) {
        launch {
            val dog = DogDatabase(getApplication()).dogDao().getDog(uuid)
            dogLiveData.value = dog
        }

    }
}