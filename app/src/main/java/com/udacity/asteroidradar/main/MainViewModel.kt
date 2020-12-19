package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    init{
        refreshData()
    }

    fun refreshData(){
        viewModelScope.launch {
            asteroidsRepository.apply {
                refreshAsteroids()
                refreshPictureOfTheDay()
            }
        }
    }

    // link to the Asteroids List LiveData on the repository
    enum class PeriodDays {ONE, SEVEN}
    val daysIncluded = MutableLiveData(PeriodDays.SEVEN)

    // Depending on the period to fetch, ask the Repository to fetch the period items from the DB
    val asteroidsList =
        Transformations.switchMap(daysIncluded) { days ->
            days?.let {
                when (days) {
                    PeriodDays.ONE   -> asteroidsRepository.asteroidsListToday
                    PeriodDays.SEVEN -> asteroidsRepository.asteroidsListUpToDefaultEndDateDays
                }
            }
        }

    // link to the Photo of the Day LiveData on the repository
    val pictureOfDay = asteroidsRepository.pictureOfDay

    // log the status of the repository
    val networkStatus =  asteroidsRepository.status


    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail
        get() = _navigateToAsteroidDetail

    fun onSleepNightClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun onSleepDataQualityNavigated() {
        _navigateToAsteroidDetail.value = null
    }
}