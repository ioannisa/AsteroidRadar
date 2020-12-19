package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.api.getDaysFromNowStr
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.getToday
import com.udacity.asteroidradar.database.getUpToDefaultEndDateDays
import com.udacity.asteroidradar.network.NETWORK_STATUS
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    private val dateStrFrom = getDaysFromNowStr(0, Constants.API_QUERY_DATE_FORMAT)
    private val dateStrUpTo = getDaysFromNowStr(Constants.DEFAULT_END_DATE_DAYS, Constants.API_QUERY_DATE_FORMAT)

    /**
     * Present data from database up to desired period
     */
    val asteroidsListUpToDefaultEndDateDays: LiveData<List<Asteroid>> =
        Transformations.map(database.getUpToDefaultEndDateDays()){
            it?.asDomainModel()
        }

    val asteroidsListToday: LiveData<List<Asteroid>> =
        Transformations.map(database.getToday()){
            it?.asDomainModel()
        }

    var status = MutableLiveData<NETWORK_STATUS>(NETWORK_STATUS.INITIALIZING)

    /**
     * Fetch Data up to the period defined by DEFAULT_END_DATE_DAYS
     */
    suspend fun refreshAsteroids(){
        withContext(Dispatchers.Main){
            try {
                status.value = NETWORK_STATUS.INITIALIZING

                // Get String json response via retrofit with scalars converter
                val scalarsStringResponse = Network.retrofitAsteroidsService.getAsteroidList(dateStrFrom, dateStrUpTo, Constants.API_KEY)

                // Transform the string response to json object
                val jsonObject = JSONObject(scalarsStringResponse)

                // Get an ArrayList<NetworkAsteroids> from the JSONObject
                val arrayListOfNetworkAsteroids = parseAsteroidsJsonResult(jsonObject)

                // set at the asteroidsList dataclass the the ArrayList<NetworkAsteroids> received
                val asteroidsList = NetworkAsteroidContainer(arrayListOfNetworkAsteroids)

                // push the fetched results to the database
                withContext(Dispatchers.IO) {
                    database.asteroidDao.insertAllAsteroids(*asteroidsList.asDatabaseModel())
                }
                status.value = NETWORK_STATUS.CONNECTED
            }
            catch (e: Exception){
                // Network Error (no internet)
                status.value = NETWORK_STATUS.DISCONNECTED
            }
        }
    }

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.asteroidDao.getPictureOfDay()){
            it?.asDomainModel()
        }

    suspend fun refreshPictureOfTheDay(){
        withContext(Dispatchers.IO){
            try {
                // Get the picture of the day via moshi converter
                val networkPictureOfDay = Network.retrofitAsteroidsService.getPictureOfDay(Constants.API_KEY)

                // push the fetched results to the database
                if (networkPictureOfDay.mediaType == "image")
                    database.asteroidDao.insertPictureOfDay(networkPictureOfDay.asDatabaseModel())
            }
            catch (e: Exception){
                // Network Error (no internet)
            }
        }
    }
}