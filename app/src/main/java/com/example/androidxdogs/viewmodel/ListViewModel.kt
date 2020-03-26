package com.example.androidxdogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidxdogs.model.*
import com.example.androidxdogs.util.NotificationHelper
import com.example.androidxdogs.util.SharedpreferanceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.NumberFormatException

class ListViewModel(application: Application):BaseViewModel(application) {

    private var prefHelper = SharedpreferanceHelper(getApplication())
    private val dogService = DogsApiClient()
    private val disposiable = CompositeDisposable()
    private var refreshTime = 5 * 60  * 1000 * 1000 * 1000L

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(){
        /*val dog1 = DogBreed("1","Husky","20","","","","")
        val dog2 = DogBreed("2","Labrador","10","","","","")
        val dog3 = DogBreed("3","GoldenRetriver","15","","","","")


        dogs.value = arrayListOf<DogBreed>(dog1,dog2,dog3)
        dogsLoadError.value = false
        loading.value = false*/
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if(updateTime != null && updateTime != 0L && (System.nanoTime() - updateTime) < refreshTime){
            fetchFromDatabase()
        }else{
            fetchFromRemote()
        }

    }

    fun refreshBypassCash(){
        fetchFromRemote()
    }

    private fun checkCacheDuration(){
        val cachePreferance = prefHelper.getCacheDuration()

        try {
            val cachePreferanceInt = cachePreferance?.toInt() ?: 5 * 60
            refreshTime = cachePreferanceInt.times( 1000 * 1000 * 1000L)
        }catch (e:NumberFormatException){
            e.printStackTrace()
        }

    }

    private fun fetchFromDatabase(){
        loading.value = true
        launch {
            val dogs = DogDatabase.invoke(getApplication()).dogDao().getAllDogs()
            dogRetrived(dogs)
            Toast.makeText(getApplication(),"Dogs Retrived From Database",Toast.LENGTH_LONG).show()
            NotificationHelper(getApplication()).createNotification()

        }
    }

    private fun fetchFromRemote(){
        loading.value = true
        disposiable.add(
            dogService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogLocally(dogList)
                        Toast.makeText(getApplication(),"Dogs Retrived From Remote",Toast.LENGTH_LONG).show()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }

    private fun dogRetrived(dogList: List<DogBreed>){
        loading.value = false
        dogs.value = dogList
        dogsLoadError.value = false
    }

    private fun storeDogLocally(list: List<DogBreed>){
        launch {
           val dao =  DogDatabase(getApplication()).dogDao()
               dao.deletAllDogs()
            val result = dao.insertAll(*list.toTypedArray())
            var i = 0
            while (i < list.size){
                list[i].uuid = result[i].toInt()
                ++i
            }
            dogRetrived(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposiable.clear()
    }
}