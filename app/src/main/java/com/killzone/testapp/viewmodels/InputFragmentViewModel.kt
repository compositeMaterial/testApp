package com.killzone.testapp.viewmodels

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.lifecycle.*
import com.killzone.testapp.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.security.KeyPairGenerator
import kotlin.random.Random

class InputFragmentViewModel : ViewModel() {

    private val _coordinatesValue = MutableLiveData<Coordinates>()
    val coordinatesValue: LiveData<Coordinates> = _coordinatesValue

    private val _navigateValue = MutableLiveData<Boolean>(false)
    val navigateValue: LiveData<Boolean> = _navigateValue

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _errorValue = MutableLiveData<Boolean>(false)
    val errorValue: LiveData<Boolean> = _errorValue


    private fun loadData(enteredNumber: Int) {

        /*viewModelScope.launch {
            try {
                val response = NetworkApi.retrofitService.getProperties()

                if (response.isSuccessful) {
                    //
                    //
                }

            } catch (e: HttpException) {
                _errorMessage.value = "Exception: ${e.message()}"
                _errorValue.value = true
            }
        }*/

       _coordinatesValue.value = getData(enteredNumber)
       _navigateValue.value = true
    }

    private fun getData(p: Int): Coordinates {
        val points = mutableListOf<Point>()

        for (i in 1..p) {
            points.add(Point(
                Random.nextDouble(-30.0, 30.0),
                Random.nextDouble(-30.0, 30.0)
            ))
        }
        return Coordinates(0, Res(points))
    }


    fun acceptNumber(n: Int) {
        loadData(n)
    }

    fun setNavigateValue() {
        _navigateValue.value = false
    }

    fun setErrorValue() {
        _errorValue.value = false
    }

}