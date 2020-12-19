package com.udacity.asteroidradar

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.udacity.asteroidradar.api.dateToStr
import com.udacity.asteroidradar.api.strToDate
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * DOMAIN LEVEL Asteroid data class
 */
@Parcelize
data class Asteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable

/**
 * RETROFIT LEVEL Asteroid data class
 */
data class NetworkAsteroidContainer(val asteroids: ArrayList<NetworkAsteroid>)
data class NetworkAsteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

/**
 * ROOM LEVEL Asteroid data class (the Room Entity - table)
 */
@Entity
data class DatabaseAsteroid(
    @PrimaryKey val id: Long, val codename: String, val closeApproachDate: Date,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

/**
 * Type Converters for Date field of the database object representing the asteroids
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}

/**
 * Database Object -> Domain Object
 */
fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate =  dateToStr(it.closeApproachDate, Constants.API_QUERY_DATE_FORMAT),
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

/**
 * Network Object -> Database Object
 */
fun NetworkAsteroidContainer.asDatabaseModel(): Array<DatabaseAsteroid> {
    return asteroids.map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = strToDate(it.closeApproachDate, Constants.API_QUERY_DATE_FORMAT),
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}